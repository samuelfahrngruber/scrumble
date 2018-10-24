Insert into sc_user values(null,'Webi','webi1234');
Insert into sc_user values(null,'Samuel','sam1234');
Insert into sc_user values(null,'Paul','paul1234');
Insert into sc_user values(null,'Simon','simon1234');
Insert into sc_user values(null,'Martin','tino1234');
Insert into sc_user values(null,'Nico','nico1234');

Insert into sc_project values(null,'Scrumble',24,null);
Insert into sc_project values(null,'Sportify',27,null);
Insert into sc_project values(null,'Caplin-DA',28,null);
Insert into sc_project values(null,'SQL-Developer Ocepek',23,null);

Insert into sc_sprint values(null,1,sysdate ,sysdate + 100,22);
Insert into sc_sprint values(null,2,sysdate + 101,sysdate + 200,22);
Insert into sc_sprint values(null,1,sysdate - 100,sysdate - 1,23);
Insert into sc_sprint values(null,2,sysdate - 200,sysdate - 101,23);
Insert into sc_sprint values(null,1,sysdate - 100,sysdate - 50,24);
Insert into sc_sprint values(null,2,sysdate - 50,sysdate - 1,24);
Insert into sc_sprint values(null,1,sysdate - 1000,sysdate - 500,25);
Insert into sc_sprint values(null,2,sysdate - 499,sysdate,25);

Insert into sc_teammember values(23,22);
Insert into sc_teammember values(24,22);
Insert into sc_teammember values(25,22);
Insert into sc_teammember values(26,22);

Insert into sc_teammember values(23,23);
Insert into sc_teammember values(24,23);
Insert into sc_teammember values(25,23);
Insert into sc_teammember values(26,23);
Insert into sc_teammember values(27,23);

Insert into sc_teammember values(23,24);
Insert into sc_teammember values(24,24);
Insert into sc_teammember values(28,24);

Insert into sc_teammember values(23,25);
Insert into sc_teammember values(25,25);
Insert into sc_teammember values(27,25);

Insert into sc_task values(null,23,26,'Testdata','Adding Testdata to Database',0,'TO_VERIFY',1,9,22);
Insert into sc_task values(null,24,25,'WPF','Creating a beatiful WPF Application',0,'IN_PROGRESS',1,9,22);
Insert into sc_task values(null,25,23,'Android','Creating a beautiful Android Application',0,'IN_PROGRESS',2,10,22);
Insert into sc_task values(null,26,24,'Webservice','Creating a beautifully running Webservice to get a Döner from Sam',0,'IN_PROGRESS',3,10,22);

Insert into sc_task values(null,23,27,'Scrumboard','Making a Scrum Board',0,'DONE',1,11,23);
Insert into sc_task values(null,24,25,'Backend','Making a the backend',0,'DONE',2,11,23);
Insert into sc_task values(null,25,23,'Frontend','Making the frontend',0,'DONE',1,12,23);
Insert into sc_task values(null,27,26,'Webservice','Creating a beautifully running Webservice',0,'DONE',2,12,23);

Insert into sc_task values(null,23,28,'CRM System','Reworking the CRM System',0,'DONE',1,13,24);
Insert into sc_task values(null,28,24,'Streamlink','Streamlink stuff',0,'DONE',1,14,24);

Insert into sc_task values(null,23,26,'SQL_Developen','Develop SQL to make Ozepek happy',0,'DONE',1,15,25);
Insert into sc_task values(null,26,27,'More SQL','MORE SQL',0,'DONE',2,15,25);
Insert into sc_task values(null,27,26,'Even More SQL','MAXIMUM SQL Development',0,'DONE',1,16,25);