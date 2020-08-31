package com.mycity4kids.ui.livestreaming

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import kotlinx.android.synthetic.main.room_list_activity.*

class RoomListActivity : BaseActivity(), View.OnClickListener,
    RoomListAdapter.RecyclerViewItemClickListener {

    val root = FirebaseDatabase.getInstance().reference.root.child("live_stream")
    val roomList = ArrayList<String>()
    private val roomListAdapter: RoomListAdapter by lazy {
        RoomListAdapter(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_list_activity)

        addRoomTextView.setOnClickListener(this)
        val llm = LinearLayoutManager(this)
        roomRecyclerView.layoutManager = llm
        roomListAdapter.setListData(roomList)
        roomRecyclerView.adapter = roomListAdapter

        root.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val set = HashSet<String>()

                val iterator = snapshot.children.iterator()
                while (iterator.hasNext()) {
                    (iterator.next() as DataSnapshot).key?.let { set.add(it) }
                }
                roomList.clear()
                roomList.addAll(set)
                roomListAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.addRoomTextView -> {
                val map = HashMap<String, Any>()
                map[inputRoomEditText.text.toString()] = ""
                root.updateChildren(map)
            }
        }
    }

    override fun onClick(view: View, position: Int) {
        val intent = Intent(this, LiveStreamingActivity::class.java)
        intent.putExtra("room", roomList[position])
        startActivity(intent)
    }
}
