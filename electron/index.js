import { app, BrowserWindow } from 'electron/main'
import serve from 'electron-serve'

serve({ directory: 'app', file: "gallery" })

app.whenReady().then(async () => {
  const win = new BrowserWindow()
  await win.loadURL("app://-")
})
