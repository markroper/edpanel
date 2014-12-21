Scholar Score 
================
Scholar Score is a data warehouse application that exposes a REST API for CRUD operations on school districts, schools, grade levels, courses, assignments, teachers, students, and student assignments and student courses.
=======
Scholar Store 
================
Scholar Store is a multi-module application designed to act as a data warehouse, analytics, and visualization platform for student performance data. 

The data warehouse component of the platform exposes a REST API to create, update, retrieve and delete resources within the platform related to school districts, individual schools, grade levels, courses, assignments, teachers, students, and student assignments and  student course performance. Functionality related to the data warehouse and its API reside in the **warehouse** module.

The platform will also have a data analytics and visualization engine that will consume data from the **warehouse** API related to student, teacher, school, and district performance and allow users to visualize that data in a a UI.  This module is not yet written, but expect to see an **analytics** module.

Domain objects and utility functionality are in the **common** module.  Other modules may have a compile dependency on the common project, for example the **warehouse** module, but the **common** module should have no dependencies on any other module.
