5/11/13  

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
  fitlibrary:  fitlibrary [built by hand ca. 2011.  Fitlibrary was developed by Rick Mugridge:  http://sourceforge.net/apps/mediawiki/fitlibrary/index.php?title=Main_Page]  

There should be no errors or warnings.  
 The lack of warnings is misleading; this project makes a lot of mis-quided use of generics (I was just learning...),
 and needs to be re-worked and re-factored to eliminate pointless generics complexity.  

The tests in the tests folder have no external dependencies except for some files that are 
created by the tests.  It may take a couple of runs of the tests for all
the files to be created, after which there should be no failures.

The tests in the testDatabase folder have dependencies on HSQLDB
  File / Import / Run/Debug / Launch Configurations; then select from the project root
   hsqldb.launch 
   hsqldb Swing.launch 
   all simulation scenario tests.launch (if you don't already have one)
   all simulation scenario database tests.launch (for the tests in testDatabase)
  run:  hsqldb 
  then: all simulation scenario database tests
 
 The fitlibrary folder contains the ParameterSpaceFixture; use of that fixture assumes you have a working 
 version of fitnesse as distributed by Rick Mugridge...more on that later, or contact me.
 
 Log4j: src/log4j.properties currently set to "log4j.rootLogger = debug..."; change to "log4j.rootLogger = error..." to cut back on verboseness   
        

There's lots to improve:
 review and rework generics (above)
 bunches of TODOs and FIXMEs
 turn MikeTest into constraints on valid combinations of parameters values (see also ParameterConstraintTest) -- thanks, Mike :)

Questions:  stevedoubleday@gmail.com

Thanks for your interest!

Steve Doubleday
  