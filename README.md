# SO-ChatBot-Driver 

### (StackOverflow Chat Bot Driver)

A little project hacked out of the following:

 - [SO-ChatBot](https://github.com/Zirak/SO-ChatBot)
 - Disappointment at the need to run SO-ChatBot in a web browser on a desktop or X11 session
 - A desire to have a dedicated chat bot in [Root Access](http://chat.stackexchange.com/rooms/118/root-access) on [chat.stackexchange.com](http://chat.stackexchange.com)
 - Motivation to learn Selenium WebDriver framework and use PhantomJS in something neat
 - That's about it, really.

## So WTF does it do?

 - You build it (either as a .jar or loose class files), and pass it command line parameters to tell it where it should live and who it should login as.
 - You download and build SO-ChatBot and PhantomJS.
 - You have a StackExchange or StackOverflow account with at least 20 rep (that you can dedicate to this purpose).
 - **Once it works**, it drives [PhantomJS](http://phantomjs.org) by invoking the command `phantomjs` from the `PATH` environment variable; logging into StackExchange; and navigating to the chat room of your choice. Then, it loads the SO-ChatBot JavaScript payload and the rest is history.

## What gives you (allquixotic) stomach ulcers?

 - The fact that PhantomJS is using a very old version of WebKit (approx. Safari 5.1 from 2011) with default **JSCore** as the JavaScript engine, instead of Chrome's superior V8.
 - The fact that the authors of SO-ChatBot felt the need to use every single experimental/advanced/brand-spanking-new JavaScript API under the sun. In fact, they're probably writing up a patch right now that is going to use an API that is being committed to V8 git next month. Hell, they probably **wrote** the damn thing.
 
 ## License
 
 Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0.txt