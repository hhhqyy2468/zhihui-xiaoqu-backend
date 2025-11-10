package com.hyu.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 */
@Data
public class TreeSelect implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    private List<TreeSelect> children;

    public TreeSelect() {
    }

    /**
     * 构造方法
     */
    public TreeSelect(Long id, String label, List<TreeSelect> children) {
        this.id = id;
        this.label = label;
        this.children = children;
    }

    /**
     * 从实体类转换为TreeSelect
     */
    public static TreeSelect buildTree(Object entity) {
        // 这里可以根据具体的实体类来实现转换逻辑
        // 暂时返回空的TreeSelect
        return new TreeSelect();
    }

    /**
     * 从列表构建树形结构
     */
    public static List<TreeSelect> buildTreeList(List<?> list) {
        // 这里可以根据具体的实体类来实现转换逻辑
        // 暂时返回空列表
        return list.stream()
                .map(TreeSelect::buildTree)
                .collect(Collectors.toList());
    }
}