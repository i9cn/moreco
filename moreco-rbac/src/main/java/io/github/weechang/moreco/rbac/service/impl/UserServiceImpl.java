package io.github.weechang.moreco.rbac.service.impl;

import com.google.common.collect.Lists;
import io.github.weechang.moreco.base.exception.BusinessException;
import io.github.weechang.moreco.base.service.impl.BaseServiceImpl;
import io.github.weechang.moreco.rbac.dao.UserDao;
import io.github.weechang.moreco.rbac.dao.UserRoleDao;
import io.github.weechang.moreco.rbac.domain.RoleMenuDomain;
import io.github.weechang.moreco.rbac.domain.UserDomain;
import io.github.weechang.moreco.rbac.domain.UserRoleDomain;
import io.github.weechang.moreco.rbac.error.RbacError;
import io.github.weechang.moreco.rbac.service.RoleMenuService;
import io.github.weechang.moreco.rbac.service.UserRoleService;
import io.github.weechang.moreco.rbac.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangwei
 * date 2018/10/27
 * time 16:24
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserDao, UserDomain> implements UserService {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleMenuService roleMenuService;

    public UserDomain save(UserDomain user){
        if (user.getId() == null){
            // 新增用户
            UserDomain saved = baseDao.findFirstByUsername(user.getUsername());
            if (saved != null){
                throw new BusinessException(RbacError.USER_EXISTED);
            }
        } else {
            // 修改用户信息
            UserDomain saved = baseDao.findOne(user.getId());
            user.setPassword(saved.getPassword());
        }
        baseDao.save(user);
        return user;
    }

    @Override
    public List<Long> findAllMenuIdByUserId(Long id) {
        // 先查询用户有哪些角色，再根据角色来获取目录id
        List<Long> userMenuIds = Lists.newArrayList();
        List<RoleMenuDomain> roleMenus = Lists.newArrayList();
        // 查询用户有哪些角色
        List<UserRoleDomain> userRoles = userRoleService.findAllByUserId(id);
        if (CollectionUtils.isNotEmpty(userRoles)) {
            // 获取所有角色有哪些目录
            for (UserRoleDomain userRole : userRoles) {
                roleMenus.addAll(roleMenuService.findAllByRoleId(userRole.getRoleId()));
            }
        }

        for (RoleMenuDomain roleMenu : roleMenus) {
            userMenuIds.add(roleMenu.getMenuId());
        }
        return userMenuIds;
    }

    @Override
    public void updatePasswordByUserId(Long id, String newPassword) {
        UserDomain user = baseDao.findOne(id);
        if (user != null) {
            user.setPassword(newPassword);
            baseDao.save(user);
        }
    }

    @Override
    public void resetPasswordByUserId(Long id) {
        UserDomain user = baseDao.findOne(id);
        if (user != null) {
            baseDao.save(user);
        }
    }
}
