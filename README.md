Scholar Score 
================
Scholar Score is a data warehouse application that exposes a REST API for CRUD operations on school districts, schools, grade levels, courses, assignments, teachers, students, student assignments and student courses. The platform is a multi-module application with a data warehouse, an analytics and visualization engine, and an ETL component used to populate the Warehouse with data from other systems.

The data warehouse component of the platform exposes a REST API. Functionality related to the data warehouse and its API reside in the **warehouse** module.

The platform will also have a data analytics and visualization engine that will consume data from the **warehouse** API related to student, teacher, school, and district performance and allow users to visualize that data in a a UI.  This module is not yet written, but expect to see an **analytics** module.

Domain objects and utility functionality are in the **common** module.  Other modules may have a compile dependency on the common project, for example the **warehouse** module, but the **common** module should have no dependencies on any other module.

Database Entity Relationship Diagram
================
![Alt text](./database/DatabaseModel.png)