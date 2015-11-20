package com.uae.tra_smart_services.adapter;

import com.uae.tra_smart_services.entities.treview.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak-buffalo on 13.11.15.
 */
public abstract class TreeViewBaseAdapter<M extends TreeViewBaseAdapter.TreeEntity>{
    private List<M> mModel;
    TreeNode root = TreeNode.root();

    public TreeViewBaseAdapter(List<M> _model){
        mModel = _model;
    }

    public TreeNode getRoot(){
        root.addChildren(createTreeNodes(mModel));
        return root;
    }

    protected abstract boolean hasChild(M _entity);

    protected abstract int getChildCount(M _entity);

    protected abstract List<TreeEntity> getChildren(TreeEntity _entity);

    protected abstract TreeNode createTreeHeaderNode(M _entity);

    protected abstract TreeNode createTreeItemNode(M _entity);

    protected TreeNode createTreeNode(M _entity){
        if(hasChild(_entity)){
            TreeNode headerNode = createTreeHeaderNode(_entity);
            headerNode.addChildren(createChildNodesFromParent(_entity));
            return headerNode;
        } else {
            return createTreeItemNode(_entity);
        }
    }

    protected List<TreeNode> createTreeNodes(List<M> _entitityList){
        List<TreeNode> resultNodes = new ArrayList<>();
        for(M entity : _entitityList){
            resultNodes.add(createTreeNode(entity));
        }
        return resultNodes;
    }

    protected List<TreeNode> createChildNodesFromParent(TreeEntity _entity) {
        List<TreeNode> treeNodes = new ArrayList<>();
        for (int i = 0; i < getChildCount((M) _entity); i++){
            TreeEntity treeEntity = getChildren(_entity).get(i);
            TreeNode treeNode = createTreeNode((M)treeEntity);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    public interface TreeEntity {
        boolean haveChild();
        void add(TreeEntity _entity);
        List<TreeEntity> getChildren();
    }
}