package com.gvs.avisacitas.main.ui.eventsQueue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gvs.avisacitas.R;
import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventsQueueAdapter extends RecyclerView.Adapter<EventsQueueAdapter.EventViewHolder> {

	private final List<CalendarEvent> events;

	public EventsQueueAdapter(List<CalendarEvent> events) {
		this.events = events;
	}

	@NonNull
	@Override
	public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_event_queue, parent, false);
		return new EventViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

		try {

			CalendarEvent event = events.get(position);

			holder.eventName.setText(event.getEventTitle());
			holder.eventMessage.setText(event.getEventDescription());
			holder.eventPhone.setText(event.getTargetPhone());
			holder.eventReason.setText(getEventReminderReason(event));
			holder.eventTime.setText(formatDate(event.getEventStartEpoch()));

			holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.default_disabled_color));

			// Si es el tercer evento, aumentar el tamaño del texto
			if (position == 2) {
				// Tamaño más grande para el tercer evento
				holder.eventName.setTextSize(24);
				holder.eventMessage.setTextSize(18);
				holder.eventPhone.setTextSize(24);
				holder.eventTime.setTextSize(24);

				holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));

				// Cambiar margen superior de eventName
				ViewGroup.MarginLayoutParams paramsName = (ViewGroup.MarginLayoutParams) holder.eventName.getLayoutParams();
				paramsName.topMargin = (int) holder.itemView.getContext().getResources().getDimension(com.intuit.sdp.R.dimen._20sdp);  // Ajusta el valor según necesites
				holder.eventName.setLayoutParams(paramsName);

				// Cambiar margen inferior de eventTime
				ViewGroup.MarginLayoutParams paramsTime = (ViewGroup.MarginLayoutParams) holder.eventTime.getLayoutParams();
				paramsTime.bottomMargin = (int) holder.itemView.getContext().getResources().getDimension(com.intuit.sdp.R.dimen._20sdp);  // Ajusta el valor según necesites
				holder.eventTime.setLayoutParams(paramsTime);
			}

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

	@Override
	public int getItemCount() {
		return events.size();
	}

	static class EventViewHolder extends RecyclerView.ViewHolder {
		TextView eventName, eventMessage, eventPhone, eventReason, eventTime;

		public EventViewHolder(@NonNull View itemView) {
			super(itemView);

			try{
				eventName = itemView.findViewById(R.id.event_name);
				eventMessage = itemView.findViewById(R.id.event_message);
				eventPhone = itemView.findViewById(R.id.event_phone);
				eventReason = itemView.findViewById(R.id.event_reason);
				eventTime = itemView.findViewById(R.id.event_time);
			}catch (Exception ex){
				LogHelper.addLogError(ex);
			}
		}
	}

	private String getEventReminderReason(CalendarEvent event) {

		try {
			event.getEventStartEpoch();
			//long currentTime = ;
			if (event.getI_60GetDateEpoch() == 0L)
				return "Recordatorio de 1h antes:";
			if (event.getI_1440GetDateEpoch() == 0L)
				return "Recordatorio de 24h antes:";
			if (event.getI_2880GetDateEpoch() == 0L)
				return "Recordatorio de 48h antes:";
			if (event.getI_createdGetDateEpoch() == 0L)
				return "Recordatorio de creación del evento:";
			return "No hay eventos que recordar";
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
		return "No hay eventos que recordar";
	}

	private String formatDate(long epochMillis) {

		try {

			ZonedDateTime zdt = Instant.ofEpochMilli(epochMillis)
					.atZone(ZoneId.systemDefault());
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			return zdt.format(dtf);

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
		return "21/10/2015 04:29";
	}

}
