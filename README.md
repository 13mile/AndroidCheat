[![Release](https://jitpack.io/v/13mile/androidcheat.svg)]
         (https://jitpack.io/#13mile/androidcheat)

# AndroidCheat


## Setup
Add the JitPack repository in your build.gradle (top level module):
```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```


And add next dependencies in the build.gradle of the module:
```gradle
dependencies {
    implementation 'com.github.13mile:androidcheat:0.0.2'
}
```



Application class onCreate() in word:
```
class App:Application() {
          override fun onCreate() {
          super.onCreate()

          Cheat.register<MainActivity, CheatActivity>()
          //{MainActivity} your initialize activity 
          // MainActivity have Intent filter (action.MAIN) And you have to specify (category.LAUNCHER).
          
          //{CheatActivity} your cheating activity.
          //{CheatActivity} inherits CheatBaseActivity.
          }
}
```


CheatAcitivity Setting and UseCase:
```
class CheatActivity : CheatBaseActivity(){
     override fun initializeMenus() {
         rightMenu.addButton("hellow cheat world"){
             //Do Somting
         }
         
         bottomMenu.addButton("hellow bottom menu"){
             //Do Somting
         }
         
         leftMenu.addButton("hellow left menu"){
             //Do Somting
         }
     }
 }    
```
