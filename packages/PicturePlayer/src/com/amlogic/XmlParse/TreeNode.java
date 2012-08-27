package com.amlogic.XmlParse;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
	private TreeNode<T> _parent;
	private List<TreeNode<T> > _children;
	public T _data;
	public TreeNode(TreeNode<T>  parent)
	{
		_children = new ArrayList<TreeNode<T> >();
		_parent = parent;
		if (parent != null)
		{
			_parent.addChild(this);
		}
	}
	public boolean isRoot()
	{
		return (_parent == null);
	}
	public TreeNode<T>  getParent()
	{
		return _parent;
	}
	public List<TreeNode<T> > getChildren()
	{
		return _children;
	}
	public void attachData(T data)
	{
		_data = data;
	}
	public T getData()
	{
		return _data;
	}
	protected void addChild(TreeNode<T>  node)
	{
		_children.add(node);
	}
}
