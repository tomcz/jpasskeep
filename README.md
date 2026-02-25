This project has been archived in favour of https://codeberg.org/tomcz/secrets as I have no desire to manage more than one cross-platform password manager project.

You can safely export JPasskeep passwords in XML format and import them into the Secrets password manager, and even share them across devices if you self-host the password manager backend.

JPasskeep
=========

A Java-based password generation and storage application. Created as an exercise in practical Java cryptograpy API
usage and because I don't really trust others with my passwords.

Usage
-----

Run using: `java -jar jpasskeep.jar <password file>`

The password file is optional. If not present, the application will attempt look for a file called `.jpasskeep` in your
home directory on start-up, but don't worry, you can tell it to look somewhere else.
