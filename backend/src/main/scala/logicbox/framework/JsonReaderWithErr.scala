package logicbox.framework

import spray.json.JsonReader

type JsonReaderWithErr[T, E] = JsonReader[Either[E, T]]
