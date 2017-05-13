package mybaby.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import me.hibb.mybaby.android.R;


public class WPAlertDialogFragment extends DialogFragment implements
    DialogInterface.OnClickListener {
    private static boolean isXMLRPC = false;
    private static boolean isLoadMore = false;
    public static WPAlertDialogFragment newInstance(String message) {
        return newInstance(message, null, false, null, null);
    }

    // XMLRPC Error
    public static WPAlertDialogFragment newInstance(String message, String error) {
        return newInstance(message, error, true, null, null);
    }

    // Load More Posts Override Warning
    public static WPAlertDialogFragment newInstance(String message, String error,
                                                    boolean loadMore) {
        return newInstance(message, error, loadMore, null, null);
    }

    public static WPAlertDialogFragment newInstance(String message, String error, boolean loadMore,
                                                    String infoTitle, String infoUrl) {
        WPAlertDialogFragment adf = new WPAlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alert-message", message);
        if (error != null) {
            bundle.putString("alert-error", error);
        }
        if (infoTitle != null && infoUrl != null) {
            bundle.putString("info-content", infoTitle);
            bundle.putString("info-url", infoUrl);
        }
        adf.setArguments(bundle);
        isLoadMore = loadMore;
        return adf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
    if (isXMLRPC) {
        String error = this.getArguments().getString("alert-error");
        if (error == null)
            error = getString(R.string.error);
        String message = this.getArguments().getString("alert-message");
        if (message == null)
            message = getString(R.string.error);
        if (error.contains("code 403") || error.contains("code 503")) {
            //invalid credentials
            b.setIcon(android.R.drawable.ic_dialog_alert);
            b.setTitle(R.string.no_connection);

//            if (MyBaby.currentBlog.isDotcomFlag()) {
//                // Remove wpcom password since it is no longer valid
//                SharedPreferences.Editor editor = PreferenceManager
//                        .getDefaultSharedPreferences(this.getActivity().getApplicationContext()).edit();
//                editor.remove(MyBaby.WPCOM_PASSWORD_PREFERENCE);
//                editor.commit();
//                b.setMessage(getResources().getText(R.string.incorrect_credentials) + " " + getResources().getText(R.string.please_sign_in));
//                b.setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent authIntent = new Intent(getActivity(), SignInActivity.class);
//                        authIntent.putExtra("wpcom", true);
//                        authIntent.putExtra("auth-only", true);
//                        getActivity().startActivity(authIntent);
//                    }
//                });
//            } else {
                b.setMessage(getResources().getText(R.string.error) + " " + getResources().getText(R.string.settings));
                b.setCancelable(true);
                b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent settingsIntent = new Intent(getActivity(), BlogPreferencesActivity.class);
//                        getActivity().startActivity(settingsIntent);
                    }
                });
                b.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
//            }
                
            return b.create();
        } else {
            b.setIcon(android.R.drawable.ic_dialog_alert);
            b.setTitle(R.string.no_connection);
            b.setMessage((error.contains("code 405")) ? error : message);
            b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            b.setCancelable(true);
            return b.create();
        }
    } else if (isLoadMore) {
        String error = this.getArguments().getString("alert-error");
        String message = this.getArguments().getString("alert-message");
            //invalid credentials
            b.setIcon(android.R.drawable.ic_dialog_alert);
            b.setTitle(error);
            b.setMessage(message);
            b.setCancelable(true);
            b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    OnDialogConfirmListener act = (OnDialogConfirmListener) getActivity();
                    act.onDialogConfirm();
                }
            });
            b.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            return b.create();
    } else {
        String infoTitle = this.getArguments().getString("info-content");
        final String infoURL = this.getArguments().getString("info-url");
        String error = this.getArguments().getString("alert-error");
        if (error != null)
            b.setTitle(error);
        else
            b.setTitle(R.string.error);
        b.setPositiveButton("OK", this);
        if (infoTitle != null && infoURL != null) {
            b.setNeutralButton(infoTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(infoURL)));
                }
            });
        }
        b.setMessage(this.getArguments().getString("alert-message"));
        return b.create();
    }
  }

  public interface OnDialogConfirmListener {
      void onDialogConfirm();
}

@Override
public void onClick(DialogInterface dialog, int which) {

}
}

