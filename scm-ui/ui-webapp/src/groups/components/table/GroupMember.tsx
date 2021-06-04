/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React from "react";
import { Link } from "react-router-dom";
import styled from "styled-components";
import { Member } from "@scm-manager/ui-types";
import { Icon } from "@scm-manager/ui-components";

type Props = {
  member: Member;
};

const StyledMember = styled.li`
  display: inline-block;
  margin-right: 0.25rem;
  padding: 0.25rem 0.75rem;
  border: 1px solid #eee;
  border-radius: 4px;

  @media (prefers-color-scheme: dark) {
    border-color: #363636;
  }
`;

export default class GroupMember extends React.Component<Props> {
  renderLink(to: string, label: string) {
    return (
      <Link to={to}>
        <Icon name="user" color="inherit" /> {label}
      </Link>
    );
  }

  showName(to: any, member: Member) {
    if (member._links.self) {
      return this.renderLink(to, member.name);
    } else {
      return member.name;
    }
  }

  render() {
    const { member } = this.props;
    const to = `/user/${member.name}`;
    return <StyledMember>{this.showName(to, member)}</StyledMember>;
  }
}
