ETL is a project that consumes the Powerschool API to inject into the scholarscore
MariaDB/MySQL database. 

ETL is broken down into the three obvious components:

1.) Extract into the model provided via Powerschool using JSON -> Java Objects using Gson
2.) Emit the IApiModel<T> implementation via either a collection ITransformCollection<T>
    or via ITransform<T> where T in both cases extends from IApiModel<X>
3.) Load into the API server via a Java client of the API server in scholarscore

Documentation on the API from PowerSchool is located in the documentation directory

#PowerSchool API idiosyncrasies

Certain API endpoints are roughly RESTful, others require using this ad-hoc direct query API.  To use this  API, we need to explicitly declare every field we will be accessing within our PowerSchool `plugin.xml`:

    https://support.powerschool.com/developer/api/authentication/access_request.html

With this table-level API, we can query for records and use an ad-hoc query language supported by the PowerSchool API. There is documentation for this in the PowerSchool API developer guide that is located in `./doc/api-developer-guide-1.6.0.zip` in this module:

    file:///Users/markroper/Downloads/api-developer-guide-1.6.0/data-access/basic-read-and-write/usage.html#searching

###Attendance
For example, a query to return attendance records, 3 per page, page number two, all columns on or after Oct. 18 2015 would be:

    https://excelacademy.powerschool.com/ws/schema/table/attendance?q=Att_Date=ge=2015-10-18&pagesize=3&page=2&projection=*
PowerSchool offers an extension query for Attendane with some extra fields joined in, of the form:

    POST /ws/schema/query/com.pearson.core.attendance.student_attendance_detail?page={page}&pagesize={pagesize}
    
    file:///Users/markroper/Downloads/api-developer-guide-1.6.0/data-access/basic-read-and-write/resources.html#get_attendance_detail
The supported endpoints to the attendance API are:

![the API](doc/attendanceapi.png)

###Assignment
There are multiple supported API endpoints for manipulating the assignment table. **TODO:** It is clearly possible to update a student score on an assignment from the doc below but less clear how to retrieve a student score on an assignment.

![the API](doc/assignmentapi.png)

###Section
The supported section API endpoints are below. **TODO:** We have not yet determined if the grade formula appears on the section.

![the API](doc/sectionapi.png)