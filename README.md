##Delivery form
The solution has to be pushed into a repo git. The URL of it
should be sent by email. As an alternative to git, the solution can be attached (.tar, .zip, etc).

##Problem
There is a call center where there are 3 types of employees: operator,
supervisor and director. The process call attention in the first instance must be attended by an operator, if
there is no free should be attended by a supervisor, and no
no free supervisors should be attended to by a director.

##Requirements

Design the modeling of necessary UML classes and diagrams to document and communicate the design.
There must be a Dispatcher class that handles the customer calls, and must contain the dispatchCall method so that 
assigns availables employees.
- The Dispatcher class must have the ability to process 10 calls at the same time (concurrently).
- Each call can last a random time between 5 and 10 seconds.
- You must have a unit test where 10 calls arrive.

##Extras / Plus

Give some solution about what happens with a call when not
There is no free employee.

Give some solution about what happens with a call when there are more than 10 concurrent calls.

Add the unit tests that are convenient.

Add code documentation.

##Consider

The project must be created with Maven.

If necessary, attach a document with the explanation of how and why he resolved the extra points, or comment on the
classes where their respective unit tests are located.

##Documentation
For Development documentations, go to "wiki" Section