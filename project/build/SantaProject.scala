
import sbt._

class SantaProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {
  val scalaToolsSnapshots = "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

  val specsDependency = "org.scala-tools.testing" %% "specs" % "1.6.5-SNAPSHOT" % "test" withSources
//  val specsDependency = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5-SNAPSHOT" % "test" withSources
}