CREATE OR REPLACE TRIGGER sc_trg_user
  BEFORE INSERT ON sc_user
  FOR EACH ROW
BEGIN
  SELECT sc_seq_user.nextval
  INTO :new.id
  FROM dual;
END;
/

CREATE OR REPLACE TRIGGER sc_trg_task
  BEFORE INSERT ON sc_task
  FOR EACH ROW
BEGIN
  SELECT sc_seq_task.nextval
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
