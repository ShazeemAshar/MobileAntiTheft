package androiddoctors.mobileantitheft;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new SessionManager(this).isIntroWatched()){
            finish();
            startActivity(new Intent(this,Activation.class));
        }
        
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.logo)
                        .title("Mobile Anti Theft")
                        .description("Do not worry about your phone even if it is lost or stolen. You can control your devices remotely from any place, any time. Remember 'Whoever Controls The Software Owns The Phone'")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        new SessionManager(IntroActivity.this).setIntroWatched(true);
                        startActivity(new Intent(IntroActivity.this,Activation.class));
                    }
                }, "Skip Intro"));

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.delete_icon)
                        .title("Remotely Delete Data")
                        .description("Remotely delete all your messages, logs, contacts and wipe phone memory by sending a command via SMS using any other phone")
                        .build());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.recovery_icon)
                        .title("Recover Phone Contacts")
                        .description("Download your backup contacts file by simply logging in to web portal of the app")
                        .build());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.lock_icon)
                        .title("Remotely Lock/Format Phone")
                        .description("Remotely lock and factory reset your phone by sending a command via SMS using any other phone")
                        .build());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.location_icon)
                        .title("Locate The Thief")
                        .description("Locate your phone by sending a command via SMS using any other phone")
                        .build());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.remember_icon)
                        .title("Remember")
                        .description("This App is not responsible for protecting removable media (i.e SIM Card and External Memory Card)." +
                                "It is recommended that you activate PIN lock on your SIM and lock your memory card manually")
                        .build());

    }

    @Override
    public void onFinish() {
        new SessionManager(this).setIntroWatched(true);
        startActivity(new Intent(IntroActivity.this,Activation.class));
    }
}
