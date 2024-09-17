package com.gvs.avisacitas.utils.links;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.gvs.avisacitas.R;
import com.gvs.avisacitas.utils.error.LogHelper;

public class LinkHelper {


	/**
	 * Configura los enlaces en el texto de un TextView de forma dinámica.
	 *
	 * @param context         El contexto necesario para acceder a los recursos.
	 * @param textView        El TextView en el que se establecerá el texto con enlaces.
	 * @param fullText        El texto completo que contiene las palabras o frases enlazadas.
	 * @param linkTexts       Array de las palabras o frases que deben enlazarse.
	 * @param links           Array con los enlaces (URLs) correspondientes para cada palabra o frase enlazada.
	 * @param colorResourceId El color de los enlaces.
	 */
	public static void setTextLinks(Context context, TextView textView, String fullText, String[] linkTexts, String[] links, int colorResourceId) {

		try {
			SpannableString spannableString = new SpannableString(fullText);

			for (int i = 0; i < linkTexts.length; i++) {
				String linkText = linkTexts[i];
				String url = links[i];

				int start = fullText.indexOf(linkText);
				int end = start + linkText.length();

				// Crea el ClickableSpan para manejar el clic en cada enlace
				ClickableSpan clickableSpan = new ClickableSpan() {
					@Override
					public void onClick(@NonNull View widget) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						context.startActivity(intent);
					}
				};

				// Aplica el ClickableSpan al texto correspondiente
				spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				// Aplica el color al texto enlazado
				spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorResourceId)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			// Establece el texto con los enlaces en el TextView
			textView.setText(spannableString);
			textView.setMovementMethod(LinkMovementMethod.getInstance());

		}catch (Exception ex){
			LogHelper.addLogError(ex);
		}
	}
}