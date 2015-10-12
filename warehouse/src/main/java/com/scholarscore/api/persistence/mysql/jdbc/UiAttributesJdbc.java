package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import com.scholarscore.api.persistence.UiAttributesPersistence;
import com.scholarscore.models.UiAttributes;

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
        String query = "from ui_attributes u join u.school s where s.id = :schoolId";
        @SuppressWarnings("unchecked")
        List<UiAttributes> uiAttributes = 
            (List<UiAttributes>) hibernateTemplate.findByNamedParam(query, "schoolId", new Long(schoolId));
        if (uiAttributes.size() == 1) {
            return uiAttributes.get(0);
        }
        return null;
    }

    @Override
    public void createUiAttributes(long schoolId, UiAttributes attrs) {
        hibernateTemplate.merge(attrs);    
    }

    @Override
    public void replaceUiAttributes(long schoolId, UiAttributes attrs) {
        hibernateTemplate.merge(attrs);    
    }

}
