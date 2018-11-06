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
) IS
  OLDPOSITION number;
  OLDSTATE VARCHAR2(50);
  
BEGIN
  Select sc_task.position into OLDPOSITION from sc_task where sc_task.id = newid;
  Select sc_task.state into OLDSTATE from sc_task where sc_task.id = newid;
  
  Update sc_task set sc_task.idresponsible = newidresponsible, sc_task.idverify = newidverify, sc_task.name = newname, sc_task.info = newinfo, sc_task.rejections = newrejections, 
  sc_task.state = newstate, sc_task.position = newposition, sc_task.idsprint = newidsprint, sc_task.idproject = newidproject where sc_task.id = newid;
  
  if OLDPOSITION != NEWPOSITION OR OLDSTATE != NEWSTATE then 
    for currentTask in (Select id, position, state from sc_task where sc_task.id != newid and (sc_task.state = oldstate or sc_task.state = newstate) and sc_Task.idsprint = newidsprint) loop
      if oldstate = newstate and oldposition > newposition and  oldposition > currentTask.position and newposition <= currentTask.position and newstate = currentTask.state then
        Update sc_task set sc_task.POSITION = sc_task.position + 1 where sc_task.id = currentTask.id;
      elsif oldstate = newstate and oldposition < newposition and oldposition < currentTask.position and newposition >= currentTask.position and newstate = currentTask.state then
        Update sc_task set sc_task.POSITION = sc_task.position - 1 where sc_task.id = currentTask.id;
      elsif oldstate != newstate and newposition <= currentTask.position  and newstate = currentTask.state then
        Update sc_task set sc_task.POSITION = sc_task.position + 1 where sc_task.id = currentTask.id;
      elsif oldstate != newstate and oldposition < currentTask.position  and oldstate = currentTask.state then
        Update sc_task set sc_task.POSITION = sc_task.position - 1 where sc_task.id = currentTask.id;
      end if;
    end loop;
  end if;
  commit;
END SC_PROC_UPDATE_TASK;