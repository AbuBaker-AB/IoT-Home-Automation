package com.example.home_automation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.home_automation.database.Device

class DeviceAdapter : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val currentDevice = getItem(position)
        holder.bind(currentDevice)
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceNameTextView: TextView = itemView.findViewById(R.id.tvDeviceName)
        private val deviceTypeTextView: TextView = itemView.findViewById(R.id.tvDeviceType)
        private val deviceStatusTextView: TextView = itemView.findViewById(R.id.tvDeviceStatus)
        private val deviceTimestampTextView: TextView = itemView.findViewById(R.id.tvDeviceTimestamp)

        fun bind(device: Device) {
            deviceNameTextView.text = device.name
            deviceTypeTextView.text = device.type.uppercase()
            deviceStatusTextView.text = if (device.isOn) "ON" else "OFF"

            // Format timestamp
            val formattedTime = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date(device.timestamp))
            deviceTimestampTextView.text = formattedTime

            // Set status color
            val statusColor = if (device.isOn) {
                android.graphics.Color.parseColor("#4CAF50") // Green for ON
            } else {
                android.graphics.Color.parseColor("#F44336") // Red for OFF
            }
            deviceStatusTextView.setTextColor(statusColor)
        }
    }

    class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem == newItem
        }
    }
}
