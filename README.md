# Task-Compsci-Project
```markdown
# Task-Compsci-Project

Creating a simple Task Manager with a calendar-backed GUI.

## Java 21 (LTS) upgrade / run instructions

Prerequisite: install a Java 21 JDK and set `JAVA_HOME` to the JDK installation, and add `%JAVA_HOME%\\bin` to your `PATH`.

- Compile:

```powershell
cd 'C:/Users/800025207/Desktop/Task-Compsci-Project'
javac -Xlint:unchecked *.java
```

- Run (launches the Swing GUI):

```powershell
java -cp . Driver
```

If you don't have a JDK, download a Java 21 build from a trusted provider (Eclipse Temurin / Adoptium, Oracle, etc.), install it, then set `JAVA_HOME` and update your `PATH`.

Notes:
- The GUI entry point is `Driver` (which launches `TaskGUI`).
- The project sources compile under modern JDKs (including Java 21).
```
Good GUI that is readable:

GUI is visually pleasing:
