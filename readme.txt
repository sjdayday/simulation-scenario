4/21/13  

simulation-scenario provides the core support for running simulations. 
https://github.com/sjdayday/simulation-scenario.git 
Other projects needed:
  [to be listed] 

Libraries:  
  log4j:  logging
  hibernate3:  persistence to hsqldb:  see testDatabase folder for tests
  jta:  compile-time requirement only for MockHibernateTransaction
  google-diff-match-patch:  downloaded google diff class; packaged by me as a jar 

There should be no errors or warnings.  
 The lack of warnings is misleading; this project makes a lot of mis-quided use of generics (I was just learning...),
 and needs to be re-worked and re-factored to eliminate pointless generics complexity.  

The tests in the tests folder have no external dependencies except for some files that are 
created by the tests.  It may take a couple of runs of the tests for all
the files to be created, after which there should be no failures.

The tests in the testDatabase folder [not yet added] have dependencies on HSQLDB....[more later] 

There's lots to improve:
 review and rework generics (above)
 bunches of TODOs and FIXMEs
 turn MikeTest into constraints on valid combinations of parameters values (see also ParameterConstraintTest) -- thanks, Mike :)

Questions:  stevedoubleday@gmail.com

Thanks for your interest!

Steve Doubleday
  