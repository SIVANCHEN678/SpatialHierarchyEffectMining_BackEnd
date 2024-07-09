package com.edu.cqupt.shemining.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// TODO 公共模块新增类

@TableName("category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntityOld {
    @TableId
    private String id;
    private Integer catLevel;
    private String label;
    private String parentId;
    private Integer isLeafs;
    private Integer isDelete;
    private Integer uid;
    private String status;
    private String userName;
    private String isFilter;
    private String isUpload;
    private String icdCode;

    @TableField(exist = false)
    private List<CategoryEntityOld> children;

    //    疾病管理新增
    @TableField(exist = false)
    private int tableNum0;
    @TableField(exist = false)
    private int tableNum1;
    @TableField(exist = false)
    private int tableNum2;

    // 5.9新增可共享用户列表
    private String uidList;


    public CategoryEntityOld(String id, int catLevel, String label, String parentId, int isLeafs, int isDelete, Integer uid, String status, String username,
                             String isUpload, String isFilter, String icdCode, String uidList) {
        this.id = id;
        this.catLevel = catLevel;
        this.label = label;
        this.parentId = parentId;
        this.isLeafs = isLeafs;
        this.isDelete = isDelete;
        this.uid = uid;
        this.status = status;
        this.userName = username;
        this.isUpload = isUpload;
        this.isFilter = isFilter;
        this.children = new ArrayList<>();
        this.icdCode = icdCode;
        this.uidList = uidList;
    }


    // 递归复制符合条件的节点
    public static CategoryEntityOld copyPrivareTreeStructure(CategoryEntityOld node, String uid) {
        if (node.isLeafs == 0 || (node.isLeafs == 1 && "0".equals(node.status) && uid.equals(node.uid))) {
            CategoryEntityOld newNode = new CategoryEntityOld(node.id, node.catLevel, node.label, node.parentId, node.isLeafs, node.isDelete, node.uid, "0", node.userName,node.isUpload,node.isFilter,node.icdCode,node.uidList);
            if (node.children != null) {
                for (CategoryEntityOld child : node.children) {
                    CategoryEntityOld copiedChild = copyPrivareTreeStructure(child,uid);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }


    public static CategoryEntityOld copyShareTreeStructure(CategoryEntityOld node, String uid) {
        System.out.println(node);
        System.out.println(uid);
        if (node.uidList == null){
            node.uidList = "";
        }
        if (node.isLeafs == 0 || (node.isLeafs == 1 && "1".equals(node.status) && (node.uidList.contains(uid)||uid.equals(node.uid)) )) {
            CategoryEntityOld newNode = new CategoryEntityOld(node.id, node.catLevel, node.label, node.parentId, node.isLeafs, node.isDelete, node.uid, "1",
                    node.userName,node.isUpload,node.isFilter,node.icdCode,node.uidList);
            if (node.children != null) {
                for (CategoryEntityOld child : node.children) {
                    CategoryEntityOld copiedChild = copyShareTreeStructure(child,uid);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }



    public static CategoryEntityOld copyCommonTreeStructure(CategoryEntityOld node) {
        if (node.isLeafs == 0 || (node.isLeafs == 1 && "2".equals(node.status))) {
            CategoryEntityOld newNode = new CategoryEntityOld(node.id, node.catLevel, node.label, node.parentId, node.isLeafs, node.isDelete, node.uid, "2", node.userName,node.isUpload,node.isFilter, node.icdCode, node.uidList);
            if (node.children != null) {
                for (CategoryEntityOld child : node.children) {
                    CategoryEntityOld copiedChild = copyCommonTreeStructure(child);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }






    public CategoryEntityOld(Object o, int i, String diseaseName, String s, int i1, int i2, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
    }

    public void addChild(CategoryEntityOld child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

}
