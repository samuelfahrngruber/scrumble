drop table sc_user cascade constraints;
drop table sc_Sprint cascade constraints;
drop table sc_task cascade constraints;
drop table sc_Project cascade constraints;
drop table sc_Teammember cascade constraints;
drop table sc_ProjectLogEntry cascade constraints;

create Table sc_User(
 id number Primary Key,
 username varchar2(30) unique,
 password varchar2(30)
);

create Table sc_Task(
 id number Primary Key,
 idResponsible number,
 idVerify number,
 name varchar2(30),
 info varchar2(500),
 rejections number,
 state varchar2(20) check(State = 'PRODUCT_BACKLOG' OR State = 'SPRINT_BACKLOG' or State = 'IN_PROGRESS' or State = 'TO_VERIFY' or State = 'DONE'),
 position number,
 idSprint number,
 idProject number,
 constraint fk_userstory_responsible foreign key(idResponsible) references sc_User(id),
 constraint fk_userstory_verify foreign key(idVerify) references sc_User(id),
 check((idSprint is null and State = 'PRODUCT_BACKLOG') or (idSprint is not null and State != 'PRODUCT_BACKLOG'))
);

create Table sc_Project(
 id number Primary Key,
 name varchar2(30),
 idProductowner number,
 idCurrentSprint number,
 constraint fk_project_user foreign key(idProductowner) references sc_User(id) 
);

create Table sc_Sprint(
  id number Primary Key,
  sprintNumber number,
  startDate date,
  deadline date,
  idProject number,
  constraint fk_Sprint_Project foreign key(idProject) references sc_Project(id)
);

create Table sc_Teammember(
 idUser number,
 idProject number,
 constraint pk_Teammember primary key(idUser,idProject),
 constraint fk_Teammember_User foreign key(idUser) references sc_User(id),
 constraint fk_Teammember_Project foreign key(idProject) references sc_Project(id)
);

create Table sc_ProjectLogEntry(
  id number primary key,
  entrydate date,
  text varchar2(200),
  idProject number,
  constraint fk_ProjectLogEntry_Project foreign key(idProject) references sc_project(id)
);

Alter Table sc_Project
  add constraint fk_project_sprint foreign key(idCurrentSprint) references sc_Sprint(id);
Alter Table sc_Task
  add  constraint fk_userstory_sprint foreign key(idSprint) references sc_Sprint(id);
Alter Table sc_Task
  add  constraint fk_userstory_project foreign key(idproject) references sc_project(id);
