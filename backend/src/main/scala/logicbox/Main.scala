package logicbox

object Main {
  def main(args: Array[String]): Unit = {
    if args.headOption == Some("cli") then
      logicbox.cli.CLIMain.main(args)
    else
      logicbox.server.ServerMain.main(args)
  }
}
