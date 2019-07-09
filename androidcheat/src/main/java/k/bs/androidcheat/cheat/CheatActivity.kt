package k.bs.androidcheat.cheat

import k.bs.androidcheat.CheatBaseActivity
import kotlinx.android.synthetic.main.activity_cheat.*

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