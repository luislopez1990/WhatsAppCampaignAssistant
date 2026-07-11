package mx.camaronpirata.campaigns

import android.app.*
import android.content.*
import android.net.ConnectivityManager
import kotlin.random.Random

class CampaignReceiver: BroadcastReceiver() {
    override fun onReceive(c:Context, i:Intent) {
        val p=c.getSharedPreferences("campaigns",Context.MODE_PRIVATE); val nm=c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fun note(title:String, body:String="Asistente de campañas")=nm.notify(Random.nextInt(),Notification.Builder(c,"campaign").setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).build())
        if(p.getBoolean("paused",false)){ note("Campaña pausada"); reschedule(c,60_000); return }
        val online=(c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork != null
        if(!online){ p.edit().putBoolean("network_pause",true).apply(); note("Campaña pausada: sin conexión"); reschedule(c,60_000); return }
        if(p.getBoolean("network_pause",false)){ p.edit().putBoolean("network_pause",false).apply(); note("Conexión recuperada: campaña reanudada") }
        val targets=p.getString("targets","")!!.lines().filter{it.isNotBlank()}; val index=p.getInt("index",0); val campaign=p.getString("active_id","")!!
        if(index>=targets.size){ note("Campaña terminada","${targets.size} destinos procesados; revisa el historial"); return }
        val target=targets[index]; val key=Store.key(campaign,target)
        if(p.getString(key,"")==DeliveryState.SENT.name){ p.edit().putInt("index",index+1).apply(); reschedule(c,1000); return }
        // La entrega efectiva se realiza por el servicio de Accesibilidad. El estado queda REVIEW
        // hasta observar una confirmación inequívoca en la interfaz; nunca se reenvía automáticamente.
        p.edit().putString("requested_target",target).putString(key,DeliveryState.REVIEW.name).putInt("index",index+1).apply()
        note("Destino preparado", "$target · requiere verificación de interfaz")
        c.packageManager.getLaunchIntentForPackage("com.whatsapp.w4b")?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)?.let { c.startActivity(it) }
        reschedule(c,Random.nextLong(3*60_000L,5*60_000L+1))
    }
    private fun reschedule(c:Context, delay:Long){ val pi=PendingIntent.getBroadcast(c,1,Intent(c,CampaignReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE); (c.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+delay,pi) }
}
