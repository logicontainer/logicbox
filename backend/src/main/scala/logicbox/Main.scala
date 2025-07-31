package logicbox

object Main {
  def main(args: Array[String]): Unit = {
    if args.headOption != None then
      logicbox.cli.CLIMain.main(args)
    else
      logicbox.server.ServerMain.main(args)
  }
}
