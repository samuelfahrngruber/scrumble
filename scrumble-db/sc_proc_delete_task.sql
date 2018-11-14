create or replace PROCEDURE SC_PROC_DELETE_TASK 
(
  OLDID IN NUMBER 
) IS
  OLDPOSITION number;
  OLDSTATE VARCHAR2(50);
  OLDIDSPRINT number;
  
BEGIN
  Select sc_task.position, sc_task.state, sc_task.idsprint into OLDPOSITION, OLDSTATE, OLDIDSPRINT from sc_task where sc_task.id = OLDID;
  Delete from sc_task where sc_task.id = OLDID;
  for currentTask in (Select id, position from sc_task where sc_task.id != OLDID and (sc_task.state = OLDSTATE) and sc_Task.idsprint = OLDIDSPRINT and sc_Task.position > OLDPOSITION) loop
    Update sc_task set position = position - 1 where currentTask.id = id;
  end loop;
  commit;
END SC_PROC_DELETE_TASK;
