package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mattg on 7/19/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Administrator extends Staff implements Serializable {
    public static final DimensionField ID = new DimensionField(Dimension.ADMINISTRATOR, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.ADMINISTRATOR, "Name");
    public static final DimensionField EMAIL_ADDRESS = new DimensionField(Dimension.ADMINISTRATOR, "Address");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
        add(EMAIL_ADDRESS);
    }};
    
    public Administrator() {
        this.setStaffRole(StaffRole.ADMIN);
        this.userType = UserType.ADMINISTRATOR;
    }

    public Administrator(Staff admin) {
        super(admin);
        setStaffRole(StaffRole.ADMIN);
        this.userType = UserType.ADMINISTRATOR;
    }




    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class AdministratorBuilder extends PersonBuilder<AdministratorBuilder, Administrator> {

        public Administrator build(){
            return super.build();
        }

        @Override
        protected AdministratorBuilder me() {
            return this;
        }

        public Administrator getInstance(){
            return new Administrator();
        }
    }
}
