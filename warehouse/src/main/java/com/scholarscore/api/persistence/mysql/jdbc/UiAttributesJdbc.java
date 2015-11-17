package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.UiAttributesPersistence;
import com.scholarscore.models.School;
import com.scholarscore.models.UiAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class UiAttributesJdbc implements UiAttributesPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    public UiAttributesJdbc() {
    }
    
    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }
    
    @Override
    public UiAttributes select(long schoolId) {
        String query = "from ui_attributes u where u.school.id = :schoolId";
        @SuppressWarnings("unchecked")
        List<UiAttributes> uiAttributes = 
            (List<UiAttributes>) hibernateTemplate.findByNamedParam(query, "schoolId", new Long(schoolId));
        if (uiAttributes.size() == 1) {
            return uiAttributes.get(0);
        }
        School s = new School();
        s.setId(schoolId);
        return UiAttributes.resolveDefaults(s);
    }

    @Override
    public Long createUiAttributes(long schoolId, UiAttributes attrs) {
        UiAttributes out = hibernateTemplate.merge(attrs);  
        return out.getId();
    }

    @Override
    public Long replaceUiAttributes(long schoolId, UiAttributes attrs) {
        UiAttributes out = hibernateTemplate.merge(attrs);  
        return out.getId();   
    }

}
