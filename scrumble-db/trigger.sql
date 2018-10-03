drop table sc_user cascade constraints;
drop table sc_userstory cascade constraints;
drop table sc_project cascade constraints;
drop table sc_sprint cascade constraints;
drop table sc_teammember cascade constraints;
drop table sc_projectlogentry cascade constraints;

drop sequence sc_seq_user;
drop sequence sc_seq_userstory;
drop sequence sc_seq_project;
drop sequence sc_seq_sprint;
drop sequence sc_seq_projectlogentry;

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
 constraint fk_userstory_verify foreign key(idVerify) references sc_User(id)
);

create Table sc_Project(
 id number Primary Key,
CREATE OR REPLACE TRIGGER sc_trg_user
  BEFORE INSERT ON sc_user
  FOR EACH ROW
BEGIN
  SELECT sc_seq_user.nextval
  INTO :new.id
  FROM dual;
END;
/

CREATE OR REPLACE TRIGGER sc_trg_userstory
  BEFORE INSERT ON sc_userstory
  FOR EACH ROW
BEGIN
  SELECT sc_seq_userstory.nextval
  INTO :new.id
  FROM dual;
END;
/

CREATE OR REPLACE TRIGGER sc_trg_project
  BEFORE INSERT ON sc_project
  FOR EACH ROW
BEGIN
  SELECT sc_seq_project.nextval
  INTO :new.id
  FROM dual;
END;
/

CREATE OR REPLACE TRIGGER sc_trg_sprint
  BEFORE INSERT ON sc_sprint
  FOR EACH ROW
BEGIN
  SELECT sc_seq_sprint.nextval
  INTO :new.id
  FROM dual;
END;
/

CREATE OR REPLACE TRIGGER sc_trg_projectlogentry
  BEFORE INSERT ON sc_projectlogentry
  FOR EACH ROW
BEGIN
  SELECT sc_seq_projectlogentry.nextval
  INTO :new.id
  FROM dual;
END;
/