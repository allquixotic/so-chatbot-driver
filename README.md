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
 
## Modifications to master.js (SO-ChatBot) to get it working with PhantomJS 1.9.x

 1. Grab the `es5-sham.min.js` from [es5-shim](https://github.com/kriskowal/es5-shim) and paste it into master.js.

 2. Paste in the following code to shim out `bind`:

```javascript
if(!Function.prototype.bind){Function.prototype.bind=function(scope){var self=this;return function(){return self.apply(scope,arguments)}}}
```

 3. Use a BlobBuilder shim. This is a particularly robust one that seems to work with PhantomJS:
 
```javascript
var BlobBuilder=BlobBuilder||self.WebKitBlobBuilder||self.MozBlobBuilder||(function(view){"use strict";var get_class=function(object){return Object.prototype.toString.call(object).match(/^\[object\s(.*)\]$/)[1]},FakeBlobBuilder=function(){},FakeBlob=function(data,type){this.data=data;this.size=data.length;this.type=type},FBB_proto=FakeBlobBuilder.prototype=[],FB_proto=FakeBlob.prototype,FileReaderSync=view.FileReaderSync,FileException=function(type){this.code=this[this.name=type]},file_ex_codes=("NOT_FOUND_ERR SECURITY_ERR ABORT_ERR NOT_READABLE_ERR ENCODING_ERR "+"NO_MODIFICATION_ALLOWED_ERR INVALID_STATE_ERR SYNTAX_ERR").split(" "),file_ex_code=file_ex_codes.length,URL=view.URL=view.URL||view.webkitURL||view,real_create_object_url,real_revoke_object_url,btoa=view.btoa,can_apply_typed_arrays=false,can_apply_typed_arrays_test=function(pass){can_apply_typed_arrays=!pass},ArrayBuffer=view.ArrayBuffer,Uint8Array=view.Uint8Array;while(file_ex_code--){FileException.prototype[file_ex_codes[file_ex_code]]=file_ex_code+1}try{if(Uint8Array){can_apply_typed_arrays_test.apply(0,new Uint8Array(1))}}catch(ex){}if(!URL.createObjectURL){URL={}}real_create_object_url=URL.createObjectURL;real_revoke_object_url=URL.revokeObjectURL;URL.createObjectURL=function(blob){var type=blob.type;if(type===null){type="application/octet-stream"}if(blob instanceof FakeBlob){if(btoa){return"data:"+type+";base64,"+btoa(blob.data)}else{return"data:"+type+","+encodeURIComponent(blob.data)}}else if(real_create_object_url){return real_create_object_url.call(URL,blob)}};URL.revokeObjectURL=function(object_url){if(object_url.substring(0,5)!=="data:"&&real_revoke_object_url){real_revoke_object_url.call(URL,object_url)}};FBB_proto.append=function(data){var bb=this;if(Uint8Array&&data instanceof ArrayBuffer){if(can_apply_typed_arrays){bb.push(String.fromCharCode.apply(String,new Uint8Array(data)))}else{var str="",buf=new Uint8Array(data),i=0,buf_len=buf.length;for(;i<buf_len;i++){str+=String.fromCharCode(buf[i])}}}else if(get_class(data)==="Blob"||get_class(data)==="File"){if(FileReaderSync){var fr=new FileReaderSync;bb.push(fr.readAsBinaryString(data))}else{throw new FileException("NOT_READABLE_ERR")}}else if(data instanceof FakeBlob){bb.push(data.data)}else{if(typeof data!=="string"){data+=""}bb.push(unescape(encodeURIComponent(data)))}};FBB_proto.getBlob=function(type){if(!arguments.length){type=null}return new FakeBlob(this.join(""),type)};FBB_proto.toString=function(){return"[object BlobBuilder]"};FB_proto.slice=function(start,end,type){var args=arguments.length;if(args<3){type=null}return new FakeBlob(this.data.slice(start,args>1?end:this.data.length),type)};FB_proto.toString=function(){return"[object Blob]"};return FakeBlobBuilder}(self));
```
	
 4. Modify the source code itself of master.js as follows:
 
```javascript
var b_b = new BlobBuilder();
b_b.append([worker_code]);
var blob = b_b.getBlob('application/javascript'),
code_url = window.URL.createObjectURL( blob );
//var blob = new Blob( [worker_code], { type : 'application/javascript' } ),
//code_url = window.URL.createObjectURL( blob );
```

 
 ## License
 
 Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0.txt