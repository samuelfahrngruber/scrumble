create or replace TRIGGER sc_trg_delete_teammember
  AFTER DELETE ON sc_teammember
  FOR EACH ROW
BEGIN
  Update sc_task set idresponsible = null where :old.iduser = sc_task.idresponsible and :old.idproject = sc_task.idproject;
  Update sc_task set idverify = null where :old.iduser = sc_task.idverify and :old.idproject = sc_task.idproject;
END;