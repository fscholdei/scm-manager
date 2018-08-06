//@flow
import React from "react";
import type { Group } from "../../types/Group";
import { translate } from "react-i18next";
import GroupMember from "./GroupMember";

type Props = {
  group: Group,
  t: string => string
};

class Details extends React.Component<Props> {
  render() {
    const { group, t } = this.props;
    return (
      <table className="table">
        <tbody>
          <tr>
            <td>{t("group.name")}</td>
            <td>{group.name}</td>
          </tr>
          <tr>
            <td>{t("group.description")}</td>
            <td>{group.description}</td>
          </tr>
          <tr>
            <td>{t("group.type")}</td>
            <td>{group.type}</td>
          </tr>
          <tr>
            <td>{this.renderMembers()}</td>
          </tr>
        </tbody>
      </table>
    );
  }

  renderMembers() {
    if (this.props.group.members.length > 0) {
      return (
        <div>
          {this.props.t("group.members")}
          <ul>
            {this.props.group._embedded.members.map((member, index) => {
              return <GroupMember key={index} member={member} />;
            })}
          </ul>
        </div>
      );
    } else {
      return;
    }
  }
}

export default translate("groups")(Details);
