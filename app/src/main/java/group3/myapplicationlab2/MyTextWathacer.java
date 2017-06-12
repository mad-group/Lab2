/*
package group3.myapplicationlab2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

*/
/**
 * Created by mc on 12/06/17.
 *//*


public class MyTextWathacer implements TextWatcher {
    private int position;

    public void MyTextWathacer(int position){
        this.position = position;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("Debug", "p:" + position);
        String amountStr = s.toString();
        if (amountStr.equals("")){
            pcAmount.setAmount(0);
        }
        else{
            pcAmount.setAmount(Double.parseDouble(amountStr));
        }

    }

    }
}
*/
