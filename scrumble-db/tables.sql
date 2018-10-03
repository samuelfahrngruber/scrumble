drop table sc_user cascade constraints;
drop table sc_Sprint cascade constraints;
drop table sc_UserStory cascade constraints;
drop table sc_Project cascade constraints;
drop table sc_Teammember cascade constraints;
drop table sc_ProjectLogEntry cascade constraints;

create Table sc_User(
 id number Primary Key,
 Username varchar2(30),
 Password varchar2(30)
);

create Table sc_UserStory(
 id number Primary Key,
 idResponsible number,
 idVerify number,
 Storyname varchar2(30),
 Info varchar2(500),
 Rejections number,
 State varchar2(20) check(State = 'PRODUCT_BACKLOG' OR State = 'SPRINT_BACKLOG' or State = 'IN_PROGRESS' or State = 'TO_VERIFY' or State = 'DONE'),
 Position number,
 idSprint number,
 constraint fk_userstory_responsible foreign key(idResponsible) references sc_User(id),
 constraint fk_userstory_verify foreign key(idVerify) references sc_User(id),
 check((idSprint is null and State = 'PRODUCT_BACKLOG') or (idSprint is not null and State != 'PRODUCT_BACKLOG'))
);

create Table sc_Project(
 id number Primary Key,
 Name varchar2(30),
 idProductowner number,
 idCurrentSprint number,
 constraint fk_project_user foreign key(idProductowner) references sc_User(id) 
);

create Table sc_Sprint(
  id number Primary Key,
  SprintNumber number,
  StartDate date,
  Deadline date,
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
  Entrydate date,
  Text varchar2(200),
  idProject number,
  constraint fk_ProjectLogEntry_Project foreign key(idProject) references sc_project(id)
);

Alter Table sc_Project
  add constraint fk_project_sprint foreign key(idCurrentSprint) references sc_Sprint(id);
Alter Table sc_UserStory
  add  constraint fk_userstory_sprint foreign key(idSprint) references sc_Sprint(id);
