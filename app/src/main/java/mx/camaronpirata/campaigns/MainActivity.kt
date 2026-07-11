package mx.camaronpirata.campaigns

import android.Manifest
import android.app.*
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import java.util.UUID

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("campaigns", MODE_PRIVATE) }
    private lateinit var status: TextView
    private lateinit var targets: EditText
    private lateinit var excluded: EditText
    private lateinit var message: EditText
    private var imageUri: Uri? = null

    override fun onCreate(b: Bundle?) {
        super.onCreate(b); createChannels(); if (android.os.Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 7)
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(28,20,28,20) }
        root.addView(TextView(this).apply { text="Asistente de campañas · WhatsApp Business"; textSize=25f })
        root.addView(TextView(this).apply { text="Herramienta local no oficial. WhatsApp puede cambiar su interfaz o limitar automatizaciones. Use promociones solo con consentimiento." })
        val tabs = RadioGroup(this).apply { orientation=RadioGroup.HORIZONTAL }
        tabs.addView(RadioButton(this).apply { id=View.generateViewId(); text="Grupos"; isChecked=true }); tabs.addView(RadioButton(this).apply { id=View.generateViewId(); text="Clientes con consentimiento" }); root.addView(tabs)
        message = field("Texto de la publicación", 3); root.addView(message)
        targets = field("Un grupo/cliente por línea", 4); root.addView(targets)
        excluded = field("Grupos excluidos (uno por línea)", 3); root.addView(excluded)
        val row = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        row.addView(button("Elegir imagen") { startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type="image/*"; addCategory(Intent.CATEGORY_OPENABLE) }, 40) })
        row.addView(button("Publicar ahora") { startCampaign(false, tabs.checkedRadioButtonId != tabs.getChildAt(0).id) })
        row.addView(button("Programar +15 min") { startCampaign(true, tabs.checkedRadioButtonId != tabs.getChildAt(0).id) })
        row.addView(button("Pausar") { prefs.edit().putBoolean("paused",true).apply(); notifyState("Campaña pausada") })
        row.addView(button("Reanudar") { prefs.edit().putBoolean("paused",false).apply(); notifyState("Campaña reanudada") })
        root.addView(row)
        root.addView(button("Activar servicio de Accesibilidad") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) })
        status=TextView(this).apply { text="Intervalo de control de ritmo: variable entre 3 y 5 minutos.\nHistorial: todavía no hay envíos."; textSize=16f }; root.addView(status)
        setContentView(ScrollView(this).apply { addView(root) })
    }
    private fun field(h:String, lines:Int)=EditText(this).apply { hint=h; minLines=lines; gravity=48 }
    private fun button(t:String, f:(View)->Unit)=Button(this).apply { text=t; setOnClickListener(f) }
    override fun onActivityResult(r:Int,c:Int,d:Intent?) { super.onActivityResult(r,c,d); if(r==40&&c==RESULT_OK) { imageUri=d?.data; imageUri?.let { contentResolver.takePersistableUriPermission(it,Intent.FLAG_GRANT_READ_URI_PERMISSION) }; status.text="Imagen seleccionada" } }
    private fun startCampaign(delayed:Boolean, clients:Boolean) {
        val all=targets.text.lines().map{it.trim()}.filter{it.isNotEmpty()}; val skip=excluded.text.lines().map{it.trim()}.toSet(); val chosen=all.filterNot{it in skip}
        if(chosen.isEmpty() || imageUri==null) { Toast.makeText(this,"Selecciona imagen y destinos",Toast.LENGTH_LONG).show(); return }
        if(clients && !prefs.getBoolean("consent_confirmed",false)) { AlertDialog.Builder(this).setTitle("Consentimiento requerido").setMessage("Confirma que estos clientes aceptaron recibir promociones y que respetarás las bajas.").setPositiveButton("Confirmo") { _,_-> prefs.edit().putBoolean("consent_confirmed",true).apply(); startCampaign(delayed,true) }.setNegativeButton("Cancelar",null).show(); return }
        val id=UUID.randomUUID().toString(); prefs.edit().putString("active_id",id).putString("targets",chosen.joinToString("\n")).putString("message",message.text.toString()).putString("image",imageUri.toString()).putInt("index",0).putBoolean("paused",false).apply()
        val whenMs=System.currentTimeMillis()+if(delayed) 15*60_000 else 1000
        val pi=PendingIntent.getBroadcast(this,1,Intent(this,CampaignReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        (getSystemService(ALARM_SERVICE) as AlarmManager).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMs,pi)
        notifyState("Campaña preparada: ${chosen.size} destinos"); status.text="Campaña $id\n${chosen.size} destinos; ${skip.size} excluidos"
    }
    private fun createChannels(){ (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel("campaign","Campañas",NotificationManager.IMPORTANCE_HIGH)) }
    private fun notifyState(s:String){ (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(10,Notification.Builder(this,"campaign").setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(s).setContentText("Asistente de campañas").build()) }
}
