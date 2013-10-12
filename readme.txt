7/5/13  

simulation-scenario provides the core support for running simulations. 
https://github.com/sjdayday/simulation-scenario.git 

Libraries:  
  log4j:  logging
  hibernate3:  persistence to hsqldb:  see testDatabase folder for tests
  jta:  compile-time requirement only for MockHibernateTransaction
  google-diff-match-patch:  downloaded google diff class; packaged by me as a jar
  hsqldb.jar:  hsqldb
  dom4j:  hibernate
  slf4j-api: hibernate  
  slf4j-log4j12: hibernate
  commons-collections:  hibernate
  antlr:  hibernate
  javassist:  hibernate

There should be no errors or warnings.  
 The lack of warnings is misleading; this project makes a lot of mis-quided use of generics (I was just learning...),
 and needs to be re-worked and re-factored to eliminate pointless generics complexity.  

Theere are currently two packages: utility and utility.integration.  
  This was done to make it possible to run the microtests against the utility package, 
  and tests that require a database (HSQLDB) by running against the integration package.
  This distinction is implemented in maven as a distinction between the unit tests (mvn package)
  and integration tests (mvn verify or mvn install).  
  
The tests in the utility.integration folder have dependencies on HSQLDB
  File / Import / Run/Debug / Launch Configurations; then select from the project root
   hsqldb.launch 
   hsqldb Swing.launch 
   all simulation scenario tests.launch (if you don't already have one)
   all simulation scenario database tests.launch (for the tests in testDatabase)
  run:  hsqldb 
  then: all simulation scenario database tests
 
Acceptance tests are written in fitnesse.  For details, see: 
  https://github.com/sjdayday/simulation-scenario-fit.git
      
Code formatting:  to keep consistency with existing formatting (but this would reset for all projects in your workspace, so export yours beforehand...):
 Preferences / Java / Formatter / import / default_code_formatter.xml  (in project root dir)         

There's lots to improve:
 review and rework generics (above)
 bunches of TODOs and FIXMEs
 turn MikeTest into constraints on valid combinations of parameters values (see also ParameterConstraintTest) -- thanks, Mike :)

Turning this into a system: 
 Assume each model is available through maven; take its pom.xml as the definition of all the code dependencies needed to run it
 Persist the scenario set output in MySQL, along with the pointers to the files for input, parameter space (and eventually, analysis files, e.g., R code)
 Persist the input and parameter space files in a git repository?  Github? ... or a file server
 Verify new scenario sets appropriately persisted
 Verify a new scenario set request replicates correctly against an existing scenario set 
 Create a ReplicationFixture to perform replication through Fitnesse...later, a GUI would be nice :)
 That gets us to local execution on one machine...next up:  deploy to a server, implement authentication/authorization
   

Questions:  stevedoubleday@gmail.com

Thanks for your interest!

Steve Doubleday
  
