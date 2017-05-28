package fk.retail.ip.segmentation.service;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.core.Constants;
import fk.retail.ip.core.entities.IPGroup;
import fk.retail.ip.core.poi.GenerateExcelCommand;
import fk.retail.ip.core.repository.GroupFsnRepository;
import fk.retail.ip.core.repository.GroupRepository;
import fk.retail.ip.core.repository.ProductDataRepository;
import fk.retail.ip.segmentation.model.GroupDownloadLineItem;
import fk.retail.ip.segmentation.model.GroupFsnRequest;
import fk.retail.ip.segmentation.model.GroupSegmentationRequest;

/**
 * Created by nidhigupta.m on 24/04/17.
 */
public class GroupService {

    private final GroupFsnRepository groupFsnRepository;
    private final ProductDataRepository productInfoRepository;
    private final GroupRepository groupRepository;
    private final GenerateExcelCommand generateExcelCommand;

    @Inject
    public GroupService(GroupFsnRepository groupFsnRepository, ProductDataRepository productInfoRepository,
                        GroupRepository groupRepository, GenerateExcelCommand generateExcelCommand) {
        this.groupFsnRepository = groupFsnRepository;
        this.productInfoRepository = productInfoRepository;
        this.groupRepository = groupRepository;
        this.generateExcelCommand = generateExcelCommand;
    }


    public List<String> getGroupInfo(String groupName) {
        List<String> fsns = groupFsnRepository.getFsns(groupName);
        return fsns;
    }

    public StreamingOutput getGroupInfoDownload(String groupName) {
        List<String> fsns = groupFsnRepository.getFsns(groupName);
        List<GroupDownloadLineItem> groupDownloadLineItems = Lists.newArrayList();
        fsns.forEach((fsn) -> groupDownloadLineItems.add(new GroupDownloadLineItem(fsn)));
        return generateExcelCommand.generateExcel(groupDownloadLineItems, Constants.GROUP_FSN_TEMPLATE) ;
    }

    public List<IPGroup> getGroups() {
        List<IPGroup> ipGroups = groupRepository.getEnabledGroups();
        return ipGroups;
    }

    public List<IPGroup> getStaticGroups() {
        List<IPGroup> ipGroups = groupRepository.getStaticGroups();
        return ipGroups;
    }

    public long createGroup(GroupFsnRequest groupFsnRequest) {
        IPGroup group = groupRepository.createGroup(groupFsnRequest.getGroupName());
        groupFsnRepository.insertFsnsForGroup(group, groupFsnRequest.getFsnList());
        return group.getId();
    }

    public void segmentFsnsToGroup(GroupSegmentationRequest groupSegmentationRequest) {
        Long groupId = groupSegmentationRequest.getGroupId();
        String query = groupSegmentationRequest.getRule();
        IPGroup group = groupRepository.findOne(groupId).get();
        List<String> fsns = productInfoRepository.getFsns(query);
        groupFsnRepository.updateGroupFsns(group, fsns);
    }


    public void updateGroup(GroupFsnRequest groupFsnRequest) {
        IPGroup group = groupRepository.findByGroupName(groupFsnRequest.getGroupName());
        groupFsnRepository.updateGroupFsns(group, groupFsnRequest.getFsnList());
    }

}
