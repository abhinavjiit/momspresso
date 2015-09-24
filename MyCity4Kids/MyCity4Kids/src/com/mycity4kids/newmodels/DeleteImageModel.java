package com.mycity4kids.newmodels;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 25-06-2015.
 */
public class DeleteImageModel  {
    ArrayList<DeleteImage> delete_files;

    public ArrayList<DeleteImage> getDelete_files() {
        return delete_files;
    }

    public void setDelete_files(ArrayList<DeleteImage> delete_files) {
        this.delete_files = delete_files;
    }


    public class DeleteImage {
        public int getFile_id() {
            return file_id;
        }

        public void setFile_id(int file_id) {
            this.file_id = file_id;
        }

        public int file_id;
    }
}


