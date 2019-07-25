[![Release](https://jitpack.io/v/13mile/androidcheat.svg)](https://jitpack.io/#13mile/androidcheat)

# AndroidCheat


# Setup    
### Add the JitPack repository in your build.gradle (top level module)    
```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```   
<br>

### And add next dependencies in the build.gradle of the module    
```gradle
dependencies {
    implementation 'com.github.13mile:androidcheat:0.1'
}
```    
<br>

### Application class onCreate() in word   
```
class App:Application() {
          override fun onCreate() {
          super.onCreate()

          Cheat.register<MainActivity, CheatActivity>(buildDate,buildType,versionName)
          //{MainActivity} your initialize activity 
          // MainActivity have Intent filter (action.MAIN) And you have to specify (category.LAUNCHER).
          
          //{CheatActivity} your cheating activity.
          //{CheatActivity} inherits CheatBaseActivity.
          
          //buildDate = System.currentTimeMillis()
          //buildType = BuildConfig.BUILD_TYPE
          //versionName = BuildConfig.VERSION_NAME
          }
}
```    
<br>

### CheatAcitivity Setting and UseCase   
```
class CheatActivity : CheatBaseActivity(){
     override fun initializeMenus() {
         rightMenu.addButton("hello cheat world"){
             //Do Somting
         }
         
         bottomMenu.addButton("hello bottom menu"){
             //Do Somting
         }
         
         leftMenu.addButton("hello left menu"){
             //Do Somting
         }
     }
 }    
```   
<br>  
 
### Manifest setting     
```
    <uses-permission
            android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            tools:remove="android:maxSdkVersion"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/> 
```
<br>

### getPermission     
```
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Cheat.getPermission(this)
    }

    override fun onResume() {
        super.onResume()
        Cheat.showDebugInfoView(this)
    }
}
```      
<br>

### floating Cheat View     
![device-2019-07-09-173805](https://user-images.githubusercontent.com/39984656/60873300-1fcc5600-a271-11e9-809b-753b8f2128df.png)     

<br>

### license     
```
MIT License

Copyright (c) 2019 13mile

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
