using ScrumbleLib;
using ScrumbleLib.Connection.Wrapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace scrumble
{
    public class DeveloperConsole
    {
        private IDevLogger logger;

        public DeveloperConsole(IDevLogger logger)
        {
            this.logger = logger;
        }

        public void Execute(string command)
        {
            try
            {
                string result = "";
                string[] parts = command.Split(' ');
                switch (parts[0].ToLower())
                {
                    case "get":
                        {
                            result += "got:\n";
                            switch (parts[1].ToLower())
                            {
                                case "project":
                                    {
                                        ProjectWrapper w = Scrumble.WrapperFactory.CreateProjectWrapper(int.Parse(parts[2]));
                                        result += w.ToJson();
                                        break;
                                    }
                                case "task":
                                    {
                                        TaskWrapper w = Scrumble.WrapperFactory.CreateTaskWrapper(int.Parse(parts[2]));
                                        result += w.ToJson();
                                        break;
                                    }
                                case "user":
                                    {
                                        UserWrapper w = Scrumble.WrapperFactory.CreateUserWrapper(int.Parse(parts[2]));
                                        result += w.ToJson();
                                        break;
                                    }
                                case "sprint":
                                    {
                                        SprintWrapper w = Scrumble.WrapperFactory.CreateSprintWrapper(int.Parse(parts[2]));
                                        result += w.ToJson();
                                        break;
                                    }
                                default:
                                    {
                                        throw new Exception("'" + parts[1] + "' is an invalid parameter for get.");
                                    }
                            }
                            break;
                        }
                    case "set":
                        {
                            if (parts[4].ToLower() != "to") throw new Exception("invalid set syntax");

                            switch (parts[1].ToLower())
                            {
                                case "project":
                                    {
                                        ProjectWrapper w = Scrumble.WrapperFactory.CreateProjectWrapper(int.Parse(parts[2]));
                                        switch (parts[3].ToLower())
                                        {
                                            case "name":
                                                {
                                                    w.Name = parts[5];
                                                    break;
                                                }
                                            case "productowner":
                                                {
                                                    w.ProductOwner = int.Parse(parts[5]);
                                                    break;
                                                }
                                            case "currentsprint":
                                                {
                                                    w.CurrentSprint = int.Parse(parts[5]);
                                                    break;
                                                }
                                            default:
                                                {
                                                    throw new Exception("invalid set syntax");
                                                }
                                        }
                                        break;
                                    }
                                case "task":
                                    {
                                        TaskWrapper w = Scrumble.WrapperFactory.CreateTaskWrapper(int.Parse(parts[2]));
                                        switch (parts[3].ToLower())
                                        {
                                            case "name":
                                                {
                                                    w.Name = parts[5];
                                                    break;
                                                }
                                            case "info":
                                                {
                                                    w.Info = parts[5];
                                                    break;
                                                }
                                            case "rejections":
                                                {
                                                    w.Rejections = int.Parse(parts[5]);
                                                    break;
                                                }
                                            case "responsibleuser":
                                                {
                                                    w.ResponsibleUser = int.Parse(parts[5]);
                                                    break;
                                                }
                                            case "verifyinguser":
                                                {
                                                    w.VerifyingUser = int.Parse(parts[5]);
                                                    break;
                                                }
                                            case "state":
                                                {
                                                    w.State = parts[5];
                                                    break;
                                                }
                                            default:
                                                {
                                                    throw new Exception("invalid set syntax");
                                                }
                                        }
                                        break;
                                    }
                                case "user":
                                    {
                                        UserWrapper w = Scrumble.WrapperFactory.CreateUserWrapper(int.Parse(parts[2]));
                                        switch (parts[3].ToLower())
                                        {
                                            case "username":
                                                {
                                                    w.Username = parts[5];
                                                    break;
                                                }
                                            default:
                                                {
                                                    throw new Exception("invalid set syntax");
                                                }
                                        }
                                        break;
                                    }
                                case "sprint":
                                    {
                                        SprintWrapper w = Scrumble.WrapperFactory.CreateSprintWrapper(int.Parse(parts[2]));
                                        switch (parts[3].ToLower())
                                        {
                                            case "number":
                                                {
                                                    w.Number = int.Parse(parts[5]);
                                                    break;
                                                }
                                            case "project":
                                                {
                                                    w.Number = int.Parse(parts[5]);
                                                    break;
                                                }
                                            default:
                                                {
                                                    throw new Exception("invalid set syntax");
                                                }
                                        }
                                        break;
                                    }
                                default:
                                    {
                                        throw new Exception("'" + parts[1] + "' is an invalid parameter for get.");
                                    }
                            }
                            break;
                        }
                    default:
                        {
                            throw new Exception("'" + parts[0] + "' - command not found.");
                        }
                }
                LogDev(result, "#FFFFFF");
            }
            catch (Exception ex)
            {
                LogDev("Failed to execute command:\n" + ex.Message, "#FF0000");
            }
        }

        private void LogDev(string str, string col)
        {
            logger.LogDev(str, col);
        }
    }
}
