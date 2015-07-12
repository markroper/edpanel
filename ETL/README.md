ETL is a project that consumes the Powerschool API to inject into the scholarscore
MariaDB/MySQL database. 

ETL is broken down into the three obvious components:

1.) Extract into the model provided via Powerschool using JSON -> Java Objects using Gson
2.) Emit the IApiModel<T> implementation via either a collection ITransformCollection<T>
    or via ITransform<T> where T in both cases extends from IApiModel<X>
3.) Load into the API server via a Java client of the API server in scholarscore

Documentation on the API from PowerSchool is located in the documentation directory