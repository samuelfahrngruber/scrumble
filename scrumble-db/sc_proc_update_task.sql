CREATE OR REPLACE PROCEDURE SC_PROC_UPDATE_TASK 
(
  NEWID IN NUMBER 
, NEWIDRESPONSIBLE IN NUMBER 
, NEWIDVERIFY IN NUMBER 
, NEWNAME IN VARCHAR2 
, NEWINFO IN VARCHAR2 
, NEWREJECTIONS IN NUMBER 
, NEWSTATE IN VARCHAR2 
, NEWPOSITION IN NUMBER 
, NEWIDSPRINT IN NUMBER 
, NEWIDPROJECT IN NUMBER 
, NEWCOLOR IN VARCHAR2
) IS
  OLDPOSITION NUMBER;
  OLDSTATE VARCHAR2(50);
  
BEGIN
  SELECT SC_TASK.POSITION INTO OLDPOSITION FROM SC_TASK WHERE SC_TASK.ID = NEWID;
  SELECT SC_TASK.STATE INTO OLDSTATE FROM SC_TASK WHERE SC_TASK.ID = NEWID;
  
  UPDATE SC_TASK SET SC_TASK.IDRESPONSIBLE = NEWIDRESPONSIBLE, SC_TASK.IDVERIFY = NEWIDVERIFY, SC_TASK.NAME = NEWNAME, SC_TASK.INFO = NEWINFO, SC_TASK.REJECTIONS = NEWREJECTIONS, 
  SC_TASK.STATE = NEWSTATE, SC_TASK.POSITION = NEWPOSITION, SC_TASK.IDSPRINT = NEWIDSPRINT, SC_TASK.IDPROJECT = NEWIDPROJECT, SC_TASK.COLOR = NEWCOLOR WHERE SC_TASK.ID = NEWID;
  
  IF OLDPOSITION != NEWPOSITION OR OLDSTATE != NEWSTATE THEN 
    FOR CURRENTTASK IN (SELECT ID, POSITION, STATE FROM SC_TASK WHERE SC_TASK.ID != NEWID AND (SC_TASK.STATE = OLDSTATE OR SC_TASK.STATE = NEWSTATE) AND SC_TASK.IDSPRINT = NEWIDSPRINT) LOOP
      IF OLDSTATE = NEWSTATE AND OLDPOSITION > NEWPOSITION AND  OLDPOSITION > CURRENTTASK.POSITION AND NEWPOSITION <= CURRENTTASK.POSITION AND NEWSTATE = CURRENTTASK.STATE THEN
        UPDATE SC_TASK SET SC_TASK.POSITION = SC_TASK.POSITION + 1 WHERE SC_TASK.ID = CURRENTTASK.ID;
      ELSIF OLDSTATE = NEWSTATE AND OLDPOSITION < NEWPOSITION AND OLDPOSITION < CURRENTTASK.POSITION AND NEWPOSITION >= CURRENTTASK.POSITION AND NEWSTATE = CURRENTTASK.STATE THEN
        UPDATE SC_TASK SET SC_TASK.POSITION = SC_TASK.POSITION - 1 WHERE SC_TASK.ID = CURRENTTASK.ID;
      ELSIF OLDSTATE != NEWSTATE AND NEWPOSITION <= CURRENTTASK.POSITION  AND NEWSTATE = CURRENTTASK.STATE THEN
        UPDATE SC_TASK SET SC_TASK.POSITION = SC_TASK.POSITION + 1 WHERE SC_TASK.ID = CURRENTTASK.ID;
      ELSIF OLDSTATE != NEWSTATE AND OLDPOSITION < CURRENTTASK.POSITION  AND OLDSTATE = CURRENTTASK.STATE THEN
        UPDATE SC_TASK SET SC_TASK.POSITION = SC_TASK.POSITION - 1 WHERE SC_TASK.ID = CURRENTTASK.ID;
      END IF;
    END LOOP;
  END IF;
  COMMIT;
END SC_PROC_UPDATE_TASK;
