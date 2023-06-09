package edu.ivankuznetsov.registerdataviabarcode.database.entity

import java.util.Date
import java.util.UUID

data class DataModel(var uuid: UUID, var fname: String, var sname: String = "", var lname: String, var company: String = "", var phone: String, var date: Date = Date())