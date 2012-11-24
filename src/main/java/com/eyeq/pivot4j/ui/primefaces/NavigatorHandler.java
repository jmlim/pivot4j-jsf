package com.eyeq.pivot4j.ui.primefaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Dimension.Type;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.PlaceHierarchiesOnAxes;
import com.eyeq.pivot4j.transform.PlaceLevelsOnAxes;
import com.eyeq.pivot4j.transform.PlaceMembersOnAxes;
import com.eyeq.pivot4j.ui.primefaces.tree.CubeNode;
import com.eyeq.pivot4j.ui.primefaces.tree.NodeSelectionFilter;

@ManagedBean(name = "navigatorHandler")
@RequestScoped
public class NavigatorHandler implements NodeSelectionFilter {

	@ManagedProperty(value = "#{pivotModelManager.model}")
	private PivotModel model;

	private CubeNode cubeNode;

	private TreeNode targetNode;

	private List<Dimension> dimensions;

	private Map<Axis, List<Hierarchy>> hierarchies;

	private Map<Hierarchy, List<Level>> levels;

	private Map<Hierarchy, List<Member>> members;

	/**
	 * @return the model
	 */
	public PivotModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(PivotModel model) {
		this.model = model;
	}

	/**
	 * @param axis
	 * @return
	 */
	protected List<Dimension> getDimensions(Axis axis) {
		if (dimensions == null) {
			this.dimensions = new ArrayList<Dimension>();

			for (Hierarchy hierarchy : getHierarchies(axis)) {
				dimensions.add(hierarchy.getDimension());
			}
		}

		return dimensions;
	}

	/**
	 * @param axis
	 * @return
	 */
	protected List<Hierarchy> getHierarchies(Axis axis) {
		if (hierarchies == null) {
			this.hierarchies = new HashMap<Axis, List<Hierarchy>>(2);
		}

		List<Hierarchy> result = hierarchies.get(axis);
		if (result == null) {
			PlaceHierarchiesOnAxes transform = model
					.getTransform(PlaceHierarchiesOnAxes.class);

			result = new ArrayList<Hierarchy>();
			hierarchies.put(axis, transform.findVisibleHierarchies(axis));
		}

		return result;
	}

	/**
	 * @param hierarchy
	 * @return
	 */
	protected List<Level> getLevels(Hierarchy hierarchy) {
		if (levels == null) {
			this.levels = new HashMap<Hierarchy, List<Level>>();
		}

		List<Level> result = levels.get(hierarchy);
		if (result == null) {
			PlaceLevelsOnAxes transform = model
					.getTransform(PlaceLevelsOnAxes.class);

			result = transform.findVisibleLevels(hierarchy);
			levels.put(hierarchy, result);
		}

		return result;
	}

	/**
	 * @param hierarchy
	 * @return
	 */
	protected List<Member> getMembers(Hierarchy hierarchy) {
		if (members == null) {
			this.members = new HashMap<Hierarchy, List<Member>>();
		}

		List<Member> result = members.get(hierarchy);
		if (result == null) {
			PlaceMembersOnAxes transform = model
					.getTransform(PlaceMembersOnAxes.class);

			result = transform.findVisibleMembers(hierarchy);
			members.put(hierarchy, result);
		}

		return result;
	}

	/**
	 * @return the cubeNode
	 */
	public CubeNode getCubeNode() {
		if (cubeNode == null && model.isInitialized()) {
			this.cubeNode = new CubeNode(model.getCube());
			cubeNode.setNodeFilter(this);
		}

		return cubeNode;
	}

	/**
	 * @param cubeNode
	 *            the cubeNode to set
	 */
	public void setCubeNode(CubeNode cubeNode) {
		this.cubeNode = cubeNode;

		this.dimensions = null;
		this.hierarchies = null;
		this.levels = null;
		this.members = null;
	}

	/**
	 * @return the cubeNode
	 * @throws OlapException
	 */
	public TreeNode getTargetNode() throws OlapException {
		if (targetNode == null && model.isInitialized()) {
			this.targetNode = new DefaultTreeNode();

			DefaultTreeNode columns = new DefaultTreeNode();
			columns.setExpanded(true);
			columns.setType("columns");

			targetNode.getChildren().add(columns);
			configureAxis(columns, Axis.COLUMNS);

			DefaultTreeNode rows = new DefaultTreeNode();
			rows.setExpanded(true);
			rows.setType("rows");

			targetNode.getChildren().add(rows);
			configureAxis(rows, Axis.ROWS);
		}

		return targetNode;
	}

	/**
	 * @param targetNode
	 *            the targetNode to set
	 */
	public void setTargetNode(TreeNode targetNode) {
		this.targetNode = targetNode;
	}

	/**
	 * @param axisRoot
	 * @param axis
	 * @throws OlapException
	 */
	protected void configureAxis(TreeNode axisRoot, Axis axis)
			throws OlapException {
		List<Hierarchy> hierarchies = getHierarchies(axis);
		for (Hierarchy hierarchy : hierarchies) {
			DefaultTreeNode hierarchyNode = new DefaultTreeNode();
			hierarchyNode.setData(hierarchy);
			hierarchyNode.setType("hierarchy");
			hierarchyNode.setExpanded(true);

			axisRoot.getChildren().add(hierarchyNode);

			if (hierarchy.getDimension().getDimensionType() == Type.MEASURE) {
				List<Member> members = getMembers(hierarchy);
				for (Member member : members) {
					DefaultTreeNode memberNode = new DefaultTreeNode();
					memberNode.setData(member);
					memberNode.setType("member");

					hierarchyNode.getChildren().add(memberNode);
				}
			} else {
				List<Level> levels = getLevels(hierarchy);
				for (Level level : levels) {
					DefaultTreeNode levelNode = new DefaultTreeNode();
					levelNode.setData(level);
					levelNode.setType("level");

					hierarchyNode.getChildren().add(levelNode);
				}
			}
		}
	}

	/**
	 * @param e
	 */
	public void onLevelDrop(DragDropEvent e) {
		// there should be a cleaner way to get data from the dropped component.
		// it's a limitation on PFs' side :
		// http://code.google.com/p/primefaces/issues/detail?id=2781
		String[] segments = e.getDragId().split(":");
		String[] indexSegments = segments[segments.length - 2].split("_");

		List<Integer> indexes = new ArrayList<Integer>(indexSegments.length);
		for (String index : indexSegments) {
			indexes.add(Integer.parseInt(index));
		}

		TreeNode node = findDraggedNode(getCubeNode(), indexes);
		System.out.println(node.getData());

		Member member = (Member) node.getData();

		createIdFromUniqueName(member.getUniqueName());
	}

	/**
	 * @param name
	 * @return
	 */
	private String createIdFromUniqueName(String name) {
		return name.replaceAll("[\\[\\]]", "").replaceAll("[\\s\\.]", "_")
				.toLowerCase();
	}

	/**
	 * @param parent
	 * @param indexes
	 * @return
	 */
	protected TreeNode findDraggedNode(TreeNode parent, List<Integer> indexes) {
		if (indexes.size() > 1) {
			return findDraggedNode(parent.getChildren().get(indexes.get(0)),
					indexes.subList(1, indexes.size()));
		} else {
			return parent.getChildren().get(indexes.get(0));
		}
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NodeSelectionFilter#isSelected(org.olap4j.metadata.Dimension)
	 */
	@Override
	public boolean isSelected(Dimension dimension) {
		return getDimensions(Axis.COLUMNS).contains(dimension)
				|| getDimensions(Axis.ROWS).contains(dimension);
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NodeSelectionFilter#isSelected(org.olap4j.metadata.Hierarchy)
	 */
	@Override
	public boolean isSelected(Hierarchy hierarchy) {
		return getHierarchies(Axis.COLUMNS).contains(hierarchy)
				|| getHierarchies(Axis.ROWS).contains(hierarchy);
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NodeSelectionFilter#isSelected(org.olap4j.metadata.Level)
	 */
	@Override
	public boolean isSelected(Level level) {
		return getLevels(level.getHierarchy()).contains(level);
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NodeSelectionFilter#isSelected(org.olap4j.metadata.Member)
	 */
	@Override
	public boolean isSelected(Member member) {
		return getMembers(member.getHierarchy()).contains(member);
	}
}
