package com.example.rfid_demo


import android.os.Bundle

import android.support.v7.app.AppCompatActivity

import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.experimental.xor

class MainActivity : AppCompatActivity() {




    private var RFID:SerialPortInterface?=null

    private var beep = byteArrayOf(0xAA.toByte(), 0xBB.toByte(),0x06.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte()
            ,0x06.toByte(),0x01.toByte(),0x01.toByte(),0x06.toByte())

    private val read = byteArrayOf(0xAA.toByte(),0xBB.toByte(),0x0D.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte()
            ,0x08.toByte(),0x06.toByte(),0x60.toByte(),0x01.toByte(),0xff.toByte(), 0xff.toByte(),
            0xff.toByte(), 0xFF.toByte(), 0xff.toByte(), 0xff.toByte(),0x6F.toByte())

    private val write = byteArrayOf(0xAA.toByte(),0xBB.toByte(),0x1D.toByte(),0x00.toByte(),0x00.toByte(), 0x00.toByte(),
            0x09.toByte(),0x06.toByte(),0x60.toByte(),0x01.toByte(),0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(),
            0x00.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(), 0x00.toByte(),0x00.toByte(),0x00.
            toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte())
    //UI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RFID = SerialPortInterface(this.applicationContext,"/dev/ttyS1")
        beep_button.setOnClickListener({
            beep[8]=0x05
            beep[9]=RFID!!.Make_BCC(beep,4,8)
            if (RFID?.RFID_send(1,beep)==1) {
              Toast.makeText(this,RFID?.RFID_read(),Toast.LENGTH_SHORT).show()         //蜂鸣器
            }

        })
        write_card.setOnClickListener( {
            var writeText =  writeText.text.toString()
            var writeByte:ByteArray= writeText.toByteArray( Charsets.UTF_8)
            for(i in 0..15)
            {
                write[i+16]=0x00
            }
            for(i in 0..(writeByte.size-1))
            {
                write[i+16]=writeByte[i]
            }
            write[32]= RFID!!.Make_BCC(write,4,31)
            if( RFID?.RFID_send(1,write)==1)
            {
                RFID?.RFID_read()
            }
         })                 //写入数据

        read_card.setOnClickListener( {
            if (RFID?.RFID_send(1,read)==1) {

               readText.setText(RFID?.RFID_read())

            }

        })

        clear_button.setOnClickListener( {
            writeText.setText("")
            readText.setText("")
        })
    }

}