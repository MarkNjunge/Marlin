package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Droplet(
        @field:Json(name = "image")
        val image: Image,

        @field:Json(name = "snapshot_ids")
        val snapshotIds: List<String>,

        @field:Json(name = "memory")
        val memory: Int,

        @field:Json(name = "backup_ids")
        val backupIds: List<Int>,

        @field:Json(name = "kernel")
        val kernel: Kernel?,

        @field:Json(name = "created_at")
        val createdAt: String,

        @field:Json(name = "volume_ids")
        val volumeIds: List<String>,

        @field:Json(name = "vcpus")
        val vcpus: Int,

        @field:Json(name = "networks")
        val networks: Networks,

        @field:Json(name = "next_backup_window")
        val nextBackupWindow: BackupWindow,

        @field:Json(name = "tags")
        val tags: List<String>,

        @field:Json(name = "features")
        val features: List<String>,

        @field:Json(name = "disk")
        val disk: Int,

        @field:Json(name = "size_slug")
        val sizeSlug: String,

        @field:Json(name = "size")
        val size: Size,

        @field:Json(name = "name")
        val name: String,

        @field:Json(name = "id")
        val id: Int,

        @field:Json(name = "locked")
        val locked: Boolean,

        @field:Json(name = "region")
        val region: Region,

        @field:Json(name = "status")
        val status: String
)