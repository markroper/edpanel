package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.MaMcasPersistence;
import com.scholarscore.models.state.ma.McasResult;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
@Transactional
public class MaMcasJdbc implements MaMcasPersistence {
    private HibernateTemplate hibernateTemplate;

    private static final String BASE_HQL = "from ma_mcas_results m " +
            "join fetch m.student st left join fetch st.homeAddress left join fetch st.mailingAddress " +
            "left join fetch st.contactMethods";

    @Override
    @SuppressWarnings("unchecked")
    public List<McasResult> selectMcasForStudent(Long schoolId, Long studentId) {
        String[] params = new String[]{"schoolId", "studentId" };
        Object[] paramValues = new Object[]{ schoolId, studentId };
        List<McasResult> objects = (List<McasResult>) hibernateTemplate.findByNamedParam(
               BASE_HQL + " where m.studentId = :studentId " +
                        "and m.schoolId = :schoolId",
                params,
                paramValues);
        return objects;
    }

    @Override
    public McasResult select(Long mcasId) {
        return hibernateTemplate.get(McasResult.class, mcasId);
    }

    @Override
    public Long insertMcasResult(Long schoolId, Long studentId, McasResult result) {
        McasResult out = hibernateTemplate.merge(result);
        return out.getId();
    }

    @Override
    public List<Long> insertMcasResults(List<McasResult> results) {
        int i = 0;
        List<Long> ids = new ArrayList<>();
        for(McasResult sa : results) {
            hibernateTemplate.save(sa);
            ids.add(sa.getId());
            //Release newly created entities from hibernates session im-memory storage
            if(i % 20 == 0) {
                hibernateTemplate.flush();
                hibernateTemplate.clear();
            }
            i++;
        }
        return ids;
    }

    @Override
    public void replaceMcasResult(Long schoolId, Long studentId, Long mcasId, McasResult result) {
        McasResult b = select(mcasId);
        result.setId(b.getId());
        hibernateTemplate.merge(result);
    }

    @Override
    public void deleteMcasResult(Long schoolId, Long studentId, Long mcasId) {
        McasResult result = select(mcasId);
        if (null != result) {
            hibernateTemplate.delete(result);
        }
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
