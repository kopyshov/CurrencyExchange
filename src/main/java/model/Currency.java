package model;

import com.google.gson.annotations.SerializedName;

public record Currency(int id,
                       String code,
                       @SerializedName("full_name")
                       String fullName,
                       String sign) {
}
