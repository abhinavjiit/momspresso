package com.mycity4kids.dbtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.database.BaseTable;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.GroupCategoryModel;
import com.mycity4kids.models.category.SubCategory;

import java.util.ArrayList;

/**
 * @author Deepanker Chaudhary
 */
public class CategoryListTable extends BaseTable {
    //	private static final String PRIMARY_KEY="_id";
    private static final String CATEGORY_ID = "categoryId";
    private static final String CREATED_AT = "created_at";
    private static final String CATEGORY_NAME = "categoryName";
    private static final String GROUP_NAME = "groupCategoryName";
    /**
     * Table Name:-
     */

    public static final String CATEOGTY_TABLE = "categoryTable";

    public static final String CREATE_CATEGORY_TABLE = "create table if not exists " +
            CATEOGTY_TABLE + "(" +
//			PRIMARY_KEY+" integer primary key," +
            CATEGORY_ID + " integer primary key," +
            GROUP_NAME + " text not null ," +
            CREATED_AT + " integer ," +
            CATEGORY_NAME + " text  )";

    public static final String DROP_QUERY = "Drop table if exists " + CATEOGTY_TABLE;

    public CategoryListTable(BaseApplication pApplication) {
        super(pApplication, CATEOGTY_TABLE);

    }


    @Override
    protected ContentValues getContentValues(BaseModel pModel) {
        CategoryModel _categoryModel = (CategoryModel) pModel;
        ContentValues _contentValue = new ContentValues();
        _contentValue.put(CATEGORY_ID, _categoryModel.getMainCategoryId());
        _contentValue.put(CREATED_AT, System.currentTimeMillis());
        _contentValue.put(CATEGORY_NAME, _categoryModel.getCategoryName());
        _contentValue.put(GROUP_NAME, _categoryModel.getGroupName());
        return _contentValue;
    }

    @Override
    protected ArrayList<BaseModel> getAllData(String pSelection, String[] pSelectionArgs) {
        ArrayList<BaseModel> categoryList = new ArrayList<BaseModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select * from " + CATEOGTY_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                CategoryModel _categoryModel = new CategoryModel();
                _categoryModel.setMainCategoryId(_cursor.getInt(_cursor.getColumnIndex(CATEGORY_ID)));
                _categoryModel.setCategoryName(_cursor.getString(_cursor.getColumnIndex(CATEGORY_NAME)));
                categoryList.add(_categoryModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }


    public ArrayList<GroupCategoryModel> getGroupData() {
        ArrayList<GroupCategoryModel> categoryList = new ArrayList<GroupCategoryModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select categoryId,groupCategoryName from " + CATEOGTY_TABLE + " group by " + GROUP_NAME + " order by " + CREATED_AT;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                GroupCategoryModel _categoryModel = new GroupCategoryModel();
                _categoryModel.setCategoryGroup(_cursor.getString(_cursor.getColumnIndex(GROUP_NAME)));
                _categoryModel.setCategoryId(_cursor.getInt(_cursor.getColumnIndex(CATEGORY_ID)));
                categoryList.add(_categoryModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }

    public ArrayList<CategoryModel> getCategoryData(String groupName) {
        ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select categoryId,categoryName from " + CATEOGTY_TABLE + " where " + GROUP_NAME + "=? ORDER BY " + CREATED_AT ;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{groupName});
            while (_cursor.moveToNext()) {
                CategoryModel _categoryModel = new CategoryModel();
                _categoryModel.setCategoryName(_cursor.getString(_cursor.getColumnIndex(CATEGORY_NAME)));
                _categoryModel.setCategoryId(_cursor.getInt(_cursor.getColumnIndex(CATEGORY_ID)));
                categoryList.add(_categoryModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }

    public ArrayList<CategoryModel> getCategoriesNameId() {
        ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select categoryId,categoryName from " + CATEOGTY_TABLE;
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, null);
            while (_cursor.moveToNext()) {
                CategoryModel _categoryModel = new CategoryModel();
                _categoryModel.setCategoryName(_cursor.getString(_cursor.getColumnIndex(CATEGORY_NAME)));
                _categoryModel.setCategoryId(_cursor.getInt(_cursor.getColumnIndex(CATEGORY_ID)));
                categoryList.add(_categoryModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }


    /**
     * this method will give all sub category   according to category id:
     *
     * @param categoryId
     * @return
     */

    public ArrayList<SubCategory> getAllSubCategory(int categoryId) {
        ArrayList<SubCategory> categoryList = new ArrayList<SubCategory>();
        Cursor _cursor = null;
        try {
            String CREATE_QUERY = "select subCategoryTable.subCategoryId,subCategoryTable.subCategoryName from categoryTable " +
                    "inner join subCategoryTable on " +
                    "categoryTable.categoryId=subCategoryTable.categoryId where categoryTable.categoryId=?";
            _cursor = mWritableDatabase.rawQuery(CREATE_QUERY, new String[]{String.valueOf(categoryId)});
            while (_cursor.moveToNext()) {
                SubCategory subCategoryModel = new SubCategory();
                subCategoryModel.setId(_cursor.getInt(_cursor.getColumnIndex(SubCategoryTable.SUB_CATEGORY_ID)));
                subCategoryModel.setName(_cursor.getString(_cursor.getColumnIndex(SubCategoryTable.SUB_CATEGORY_NAME)));
                categoryList.add(subCategoryModel);
            }
        } catch (Exception e) {
            Log.e(CATEOGTY_TABLE, "" + e);
        } finally {
            closeCursor(_cursor);
        }
        return categoryList;
    }

}
