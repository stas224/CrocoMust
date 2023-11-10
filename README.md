# CrocoMust - Android-app for playing Crocodile

### Android app for playing Crocodile.
- Implemented two modes of complexity of the proposed words
- The ability to start the game again or continue previous (words that have already been will not come across)
- Ability to view the history of word change
  
- External SQLite database is connected
- Implemented a dark theme
- Support for two languages (Russian, English), the output words correspond to the language of the system
- Saving the state of activity with the game when the screen is turned over (including the dialog with the rules) and re-entering the activity of the game

### Problems encountered during development and their solutions:
- There was a problem with connecting to an external database from assets -> When the application is first started, the database is transferred to internal files, and then to the internal database, after which the database becomes readable
- There was a problem with displaying the application in a horizontal position -> creating a separate xml file for landscape orientation


### Welcome Screen
[![first-picture.png](https://i.postimg.cc/T3LDghJQ/first-picture.png)](https://postimg.cc/4YGyTfMh)
### Game Screen
[![second-picture.png](https://i.postimg.cc/85KpZm1v/second-picture.png)](https://postimg.cc/wyNYvJRx)
### History screen
[![third-picture.png](https://i.postimg.cc/XqBbQM1M/third-picture.png)](https://postimg.cc/64KgpPNz)
